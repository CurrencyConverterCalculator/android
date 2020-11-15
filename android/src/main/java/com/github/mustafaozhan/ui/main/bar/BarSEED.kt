/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.ui.main.bar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mustafaozhan.data.model.Currency

// State
@Suppress("ConstructorParameterNaming")
data class BarState(
    private val _state: MutableBarState
) {
    val currencyList: LiveData<MutableList<Currency>> = _state._currencyList
    val loading: LiveData<Boolean> = _state._loading
    val enoughCurrency: LiveData<Boolean> = _state._enoughCurrency
}

@Suppress("ConstructorParameterNaming")
data class MutableBarState(
    val _currencyList: MutableLiveData<MutableList<Currency>> = MutableLiveData(),
    val _loading: MutableLiveData<Boolean> = MutableLiveData(true),
    val _enoughCurrency: MutableLiveData<Boolean> = MutableLiveData(false)
)

// Event
interface BarEvent {
    fun onItemClick(currency: Currency)
    fun onSelectClick()
}

// Effect
sealed class BarEffect
data class ChangeBaseNavResultEffect(val newBase: String) : BarEffect()
object OpenCurrenciesEffect : BarEffect()
