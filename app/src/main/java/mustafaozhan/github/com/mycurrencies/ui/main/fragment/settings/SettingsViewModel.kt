package mustafaozhan.github.com.mycurrencies.ui.main.fragment.settings

import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import mustafaozhan.github.com.mycurrencies.base.viewmodel.BaseDataViewModel
import mustafaozhan.github.com.mycurrencies.data.preferences.PreferencesRepository
import mustafaozhan.github.com.mycurrencies.data.room.dao.CurrencyDao
import mustafaozhan.github.com.mycurrencies.function.extension.insertInitialCurrencies
import mustafaozhan.github.com.mycurrencies.function.extension.removeUnUsedCurrencies
import mustafaozhan.github.com.mycurrencies.function.scope.either
import mustafaozhan.github.com.mycurrencies.model.Currencies
import mustafaozhan.github.com.mycurrencies.model.Currency

/**
 * Created by Mustafa Ozhan on 2018-07-12.
 */
class SettingsViewModel(
    preferencesRepository: PreferencesRepository,
    private val currencyDao: CurrencyDao
) : BaseDataViewModel(preferencesRepository) {

    val settingsViewStateLiveData: MutableLiveData<SettingsViewState> = MutableLiveData()

    private var currencyList: MutableList<Currency> = mutableListOf()

    override fun onLoaded(): Completable {
        return Completable.complete()
    }

    fun initData() {
        currencyList.clear()

        if (mainData.firstRun) {
            currencyDao.insertInitialCurrencies()
            preferencesRepository.updateMainData(firstRun = false)
        }

        currencyDao.getAllCurrencies().removeUnUsedCurrencies()?.let {
            currencyList.addAll(it)
        }
    }

    fun updateCurrencyState(value: Int, txt: String? = null) {
        txt?.let { name ->
            currencyList.find { it.name == name }?.isActive = value
            currencyDao.updateCurrencyStateByName(name, value)
        } ?: updateAllCurrencyState(value)

        if (value == 0) verifyCurrentBase()

        if (currencyList.filter { it.isActive == 1 }.size < MINIMUM_ACTIVE_CURRENCY) {
            settingsViewStateLiveData.postValue(SettingsViewState.FewCurrency)
        }
    }

    fun filterList(txt: String) = currencyList
        .filter { currency ->
            currency.name.contains(txt, true) ||
                currency.longName.contains(txt, true) ||
                currency.symbol.contains(txt, true)
        }
        .toMutableList()
        .let {
            settingsViewStateLiveData.postValue(
                if (it.isEmpty()) SettingsViewState.NoResult else SettingsViewState.Success(it)
            )
        }

    private fun updateAllCurrencyState(value: Int) {
        currencyList.forEach { it.isActive = value }
        currencyDao.updateAllCurrencyState(value)
    }

    private fun verifyCurrentBase() = mainData.currentBase
        .either(
            { equals(Currencies.NULL) },
            { base ->
                currencyList
                    .filter { it.name == base.toString() }
                    .toList().firstOrNull()?.isActive == 0
            }
        )?.let { setCurrentBase(currencyList.firstOrNull { it.isActive == 1 }?.name) }
}
