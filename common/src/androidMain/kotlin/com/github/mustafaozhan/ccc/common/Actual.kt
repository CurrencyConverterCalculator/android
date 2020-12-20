/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */

package com.github.mustafaozhan.ccc.common

import com.github.mustafaozhan.ccc.common.di.DATABASE_NAME
import com.github.mustafaozhan.ccc.common.model.PlatformType
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platform = PlatformType.ANDROID

actual val platformCoroutineContext: CoroutineContext = Dispatchers.IO

actual val platformCommonModule: Module = module {
    single<Settings> { AndroidSettings(get()) }
    single {
        CurrencyConverterCalculatorDatabase(
            AndroidSqliteDriver(
                CurrencyConverterCalculatorDatabase.Schema,
                get(),
                DATABASE_NAME
            )
        )
    }
}

actual fun runTest(block: suspend (scope: CoroutineScope) -> Unit) = runBlocking { block(this) }
