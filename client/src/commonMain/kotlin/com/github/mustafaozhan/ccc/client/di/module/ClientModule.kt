package com.github.mustafaozhan.ccc.client.di.module

import com.github.mustafaozhan.ccc.client.di.viewModelDefinition
import com.github.mustafaozhan.ccc.client.viewmodel.adremove.AdRemoveViewModel
import com.github.mustafaozhan.ccc.client.viewmodel.bar.BarViewModel
import com.github.mustafaozhan.ccc.client.viewmodel.calculator.CalculatorViewModel
import com.github.mustafaozhan.ccc.client.viewmodel.currencies.CurrenciesViewModel
import com.github.mustafaozhan.ccc.client.viewmodel.main.MainViewModel
import com.github.mustafaozhan.ccc.client.viewmodel.settings.SettingsViewModel
import org.koin.dsl.module

var clientModule = module {
    viewModelDefinition { SettingsViewModel(get(), get(), get(), get()) }
    viewModelDefinition { MainViewModel(get()) }
    viewModelDefinition { CurrenciesViewModel(get(), get()) }
    viewModelDefinition { CalculatorViewModel(get(), get(), get(), get()) }
    viewModelDefinition { BarViewModel(get()) }
    viewModelDefinition { AdRemoveViewModel(get()) }
}
