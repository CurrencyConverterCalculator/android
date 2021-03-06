/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.ccc.client.base

import com.github.mustafaozhan.ccc.client.di.module.clientModule
import com.github.mustafaozhan.ccc.common.di.modules.apiModule
import com.github.mustafaozhan.ccc.common.di.modules.getDatabaseModule
import com.github.mustafaozhan.ccc.common.di.modules.getSettingsModule
import com.github.mustafaozhan.logmob.initLogger
import com.github.mustafaozhan.logmob.kermit
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseViewModelTest<ViewModelType> {
    protected lateinit var koin: Koin
    protected abstract val viewModel: ViewModelType

    @BeforeTest
    fun setup() {
        initLogger(true)
        startKoin {
            modules(
                clientModule,
                apiModule,
                getDatabaseModule(true),
                getSettingsModule(true)
            )
        }.also {
            koin = it.koin
        }
        kermit.d { "BaseViewModelTest setup" }
    }

    @AfterTest
    fun destroy() {
        kermit.d { "BaseViewModelTest destroy" }
        stopKoin()
    }
}
