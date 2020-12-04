/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.temp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponseV2(
    @SerialName("base") var base: String,
    @SerialName("date") var date: String? = null,
    @SerialName("rates") var rates: RatesV2
)