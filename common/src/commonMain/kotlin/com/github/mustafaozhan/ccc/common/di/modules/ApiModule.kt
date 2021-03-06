package com.github.mustafaozhan.ccc.common.di.modules

import com.github.mustafaozhan.ccc.common.api.ApiFactory
import com.github.mustafaozhan.ccc.common.api.ApiRepository
import com.github.mustafaozhan.ccc.common.api.ApiRepositoryImpl
import org.koin.dsl.module

val apiModule = module {
    factory { ApiFactory() }

    single<ApiRepository> { ApiRepositoryImpl(get()) }
}
