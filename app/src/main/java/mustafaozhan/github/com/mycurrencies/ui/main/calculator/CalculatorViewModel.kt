/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package mustafaozhan.github.com.mycurrencies.ui.main.calculator

import androidx.lifecycle.viewModelScope
import com.github.mustafaozhan.basemob.lifecycle.MutableSingleLiveData
import com.github.mustafaozhan.basemob.lifecycle.SingleLiveData
import com.github.mustafaozhan.basemob.viewmodel.BaseViewModel
import com.github.mustafaozhan.scopemob.mapTo
import com.github.mustafaozhan.scopemob.notSameAs
import com.github.mustafaozhan.scopemob.whether
import com.github.mustafaozhan.scopemob.whetherNot
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mustafaozhan.github.com.mycurrencies.data.api.ApiRepository
import mustafaozhan.github.com.mycurrencies.data.preferences.PreferencesRepository
import mustafaozhan.github.com.mycurrencies.data.room.currency.CurrencyRepository
import mustafaozhan.github.com.mycurrencies.data.room.offlineRates.OfflineRatesRepository
import mustafaozhan.github.com.mycurrencies.model.Currency
import mustafaozhan.github.com.mycurrencies.model.CurrencyResponse
import mustafaozhan.github.com.mycurrencies.model.Rates
import mustafaozhan.github.com.mycurrencies.ui.main.MainActivityData.Companion.MINIMUM_ACTIVE_CURRENCY
import mustafaozhan.github.com.mycurrencies.ui.main.calculator.CalculatorData.Companion.CHAR_DOT
import mustafaozhan.github.com.mycurrencies.ui.main.calculator.CalculatorData.Companion.KEY_AC
import mustafaozhan.github.com.mycurrencies.ui.main.calculator.CalculatorData.Companion.KEY_DEL
import mustafaozhan.github.com.mycurrencies.ui.main.calculator.CalculatorData.Companion.MAXIMUM_INPUT
import mustafaozhan.github.com.mycurrencies.util.extension.calculateResult
import mustafaozhan.github.com.mycurrencies.util.extension.getFormatted
import mustafaozhan.github.com.mycurrencies.util.extension.getThroughReflection
import mustafaozhan.github.com.mycurrencies.util.extension.removeUnUsedCurrencies
import mustafaozhan.github.com.mycurrencies.util.extension.toPercent
import mustafaozhan.github.com.mycurrencies.util.extension.toRate
import mustafaozhan.github.com.mycurrencies.util.extension.toSupportedCharacters
import mustafaozhan.github.com.mycurrencies.util.extension.toUnit
import org.mariuszgromada.math.mxparser.Expression
import timber.log.Timber

@Suppress("TooManyFunctions")
class CalculatorViewModel(
    val preferencesRepository: PreferencesRepository,
    private val apiRepository: ApiRepository,
    private val currencyRepository: CurrencyRepository,
    private val offlineRatesRepository: OfflineRatesRepository
) : BaseViewModel(), CalculatorEvent {

    // region SEED
    private val _state = CalculatorStateBacking()
    val state = CalculatorState(_state)

    private val _effect = MutableSingleLiveData<CalculatorEffect>()
    val effect: SingleLiveData<CalculatorEffect> = _effect

    val data = CalculatorData()

    fun getEvent() = this as CalculatorEvent
    // endregion

    init {
        with(_state) {
            _loading.value = true
            _base.value = preferencesRepository.currentBase
            _input.value = ""

            _base.addSource(state.base) {
                currentBaseChanged(it)
            }
            _input.addSource(state.input) { input ->
                _loading.value = true
                calculateOutput(input)
            }

            viewModelScope.launch {
                currencyRepository.getActiveCurrencies().collect {
                    _currencyList.value = it.removeUnUsedCurrencies()
                }
            }
        }
    }

    private fun getRates() = data.rates?.let { rates ->
        calculateConversions(rates)
    } ?: viewModelScope.launch {
        subscribeService(
            apiRepository.getRatesByBase(preferencesRepository.currentBase),
            ::rateDownloadSuccess,
            ::rateDownloadFail
        )
    }

    private fun rateDownloadSuccess(currencyResponse: CurrencyResponse) = viewModelScope.launch {
        currencyResponse.toRate().let {
            data.rates = it
            calculateConversions(it)
            offlineRatesRepository.insertOfflineRates(it)
        }
    }.toUnit()

    private fun rateDownloadFail(t: Throwable) = viewModelScope.launch {
        Timber.w(t, "rate download failed 1s time out")

        offlineRatesRepository.getOfflineRatesByBase(
            preferencesRepository.currentBase
        )?.let { offlineRates ->
            calculateConversions(offlineRates)
            _effect.value = OfflineSuccessEffect(offlineRates.date)
        } ?: subscribeService(
            apiRepository.getRatesByBaseLongTimeOut(preferencesRepository.currentBase),
            ::rateDownloadSuccess,
            ::rateDownloadFailLongTimeOut
        )
    }.toUnit()

    private fun rateDownloadFailLongTimeOut(t: Throwable) {
        Timber.w(t, "rate download failed on long time out")
        state.currencyList.value?.size
            ?.whether { it > 1 }
            ?.let { _effect.value = ErrorEffect }
    }

    private fun calculateOutput(input: String) = Expression(input.toSupportedCharacters().toPercent())
        .calculate()
        .mapTo { if (isNaN()) "" else getFormatted() }
        .whether { length <= MAXIMUM_INPUT }
        ?.let { output ->
            _state._output.value = output
            state.currencyList.value?.size
                ?.whether { it < MINIMUM_ACTIVE_CURRENCY }
                ?.whetherNot { state.input.value.isNullOrEmpty() }
                ?.let { _effect.value = FewCurrencyEffect }
                ?: run { getRates() }
        } ?: run {
        _effect.value = MaximumInputEffect
        _state._input.value = input.dropLast(1)
        _state._loading.value = false
    }

    private fun calculateConversions(rates: Rates?) = with(_state) {
        _currencyList.value = _currencyList.value?.onEach {
            it.rate = rates.calculateResult(it.name, _output.value)
        }
        _loading.value = false
    }

    private fun currentBaseChanged(newBase: String) {
        data.rates = null
        preferencesRepository.currentBase = newBase

        _state._input.value = _state._input.value

        viewModelScope.launch {
            _state._symbol.value = currencyRepository.getCurrencyByName(newBase)?.symbol ?: ""
        }
    }

    fun verifyCurrentBase() = _state._base.value
        ?.notSameAs { preferencesRepository.currentBase }
        ?.let {
            _state._base.postValue(preferencesRepository.currentBase)
        }

    // region Event
    override fun onKeyPress(key: String) {
        when (key) {
            KEY_AC -> {
                _state._input.value = ""
                _state._output.value = ""
            }
            KEY_DEL -> state.input.value
                ?.whetherNot { isEmpty() }
                ?.apply {
                    _state._input.value = substring(0, length - 1)
                }
            else -> _state._input.value = if (key.isEmpty()) "" else state.input.value.toString() + key
        }
    }

    override fun onItemClick(currency: Currency, conversion: String) = with(_state) {
        var finalResult = conversion

        while (finalResult.length > MAXIMUM_INPUT) {
            finalResult = finalResult.dropLast(1)
        }

        if (finalResult.last() == CHAR_DOT) {
            finalResult = finalResult.dropLast(1)
        }

        _base.value = currency.name
        _input.value = finalResult
    }

    override fun onItemLongClick(currency: Currency): Boolean {
        _effect.value = LongClickEffect("1 ${preferencesRepository.currentBase} = " +
            "${data.rates?.getThroughReflection<Double>(currency.name)} " +
            currency.getVariablesOneLine(),
            currency.name
        )
        return true
    }

    override fun onBarClick() {
        _effect.value = ReverseSpinner
    }

    override fun onSpinnerItemSelected(base: String) {
        _state._base.value = base
    }
    // endregion
}
