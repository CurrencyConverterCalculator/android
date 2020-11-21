/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */

package com.github.mustafaozhan.ccc.common.di

import com.github.mustafaozhan.ccc.common.kermit
import com.github.mustafaozhan.ccc.common.repository.PlatformRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(vararg modules: Module?) = startKoin {
    kermit.d { "Koin initKoin" }
    modules.forEach { it?.let { modules(it) } }
    modules(commonModule)
}

var commonModule: Module = module {
    single { PlatformRepository() }
}