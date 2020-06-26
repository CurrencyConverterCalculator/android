/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package mustafaozhan.github.com.ui.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mustafaozhan.github.com.ui.main.MainActivity
import mustafaozhan.github.com.ui.slider.SliderActivity
import mustafaozhan.github.com.ui.splash.SplashActivity

@Suppress("unused")
@Module
abstract class ActivityInjectionModule {

    @ContributesAndroidInjector
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributesSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributesSliderActivity(): SliderActivity
}