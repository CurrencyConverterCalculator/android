package mustafaozhan.github.com.mycurrencies.main.fragment

import android.arch.lifecycle.MutableLiveData
import mustafaozhan.github.com.mycurrencies.base.BaseViewModel
import mustafaozhan.github.com.mycurrencies.base.model.MainData
import mustafaozhan.github.com.mycurrencies.room.model.Currency
import mustafaozhan.github.com.mycurrencies.main.fragment.model.CurrencyResponse
import mustafaozhan.github.com.mycurrencies.main.fragment.model.Rates
import mustafaozhan.github.com.mycurrencies.room.dao.CurrencyDao
import mustafaozhan.github.com.mycurrencies.tools.Currencies
import mustafaozhan.github.com.mycurrencies.tools.insertInitialCurrencies
import org.mariuszgromada.math.mxparser.Expression
import javax.inject.Inject

/**
 * Created by Mustafa Ozhan on 2018-07-12.
 */
class MainFragmentViewModel : BaseViewModel() {

    override fun inject() {
        viewModelComponent.inject(this)
    }

    @Inject
    lateinit var currencyDao: CurrencyDao

    val currenciesLiveData: MutableLiveData<Rates> = MutableLiveData()
    var currencyList: MutableList<Currency> = mutableListOf()

    lateinit var currentBase: Currencies
    lateinit var baseCurrency: Currencies

    private var firstCache: Boolean = true
    private var firstTime: Boolean = true

    var input: String = ""
    var output: String = "0.0"

    fun getCurrencies() {
        subscribeService(dataManager.getAllOnBase(currentBase),
                ::eventDownloadSuccess, ::eventDownloadFail)

    }

    private fun eventDownloadSuccess(currencyResponse: CurrencyResponse) {
        currencyResponse.rates
        currenciesLiveData.postValue(currencyResponse.rates)

    }

    private fun eventDownloadFail(t: Throwable) {
        t.printStackTrace()
    }

    fun initData() {
        currencyList.clear()
        if (firstTime) {
            currencyDao.insertInitialCurrencies()
            firstTime = false
        }
        currencyDao.getActiveCurrencies().forEach {
            currencyList.add(it)
        }
    }

    fun calculate(text: String?): String {
        var result: String? = null

        if (text != null) {
            result = if (text.contains("%"))
                Expression(text.replace("%", "/100*")).calculate().toString()
            else
                Expression(text).calculate().toString()
        }

        return result.toString()
    }


    fun setCurrentBase(newBase: String) {
        currencyList.filter {
            it.name == currentBase.toString()
        }.forEach {
            it.isActive = 1
        }

        currentBase = Currencies.valueOf(newBase)
        currencyList.filter {
            it.name == newBase
        }.forEach {
            it.isActive = 0
        }
    }


    fun checkList() {
        currencyList.filter {
            it.name == currentBase.toString()
        }.forEach {
            it.isActive = 0
        }
    }

    fun loadPreferences() {
        val mainData = dataManager.loadMainData()
        firstTime = mainData.firstRun
        firstCache = mainData.firstCache
        currentBase = mainData.currentBase
        baseCurrency = mainData.baseCurrency
    }

    fun savePreferences() {
        dataManager.persistMainData(MainData(firstTime,firstCache, baseCurrency, currentBase))
    }

}


