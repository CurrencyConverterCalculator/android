/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.ui.viewmodel

import com.github.mustafaozhan.data.api.ApiRepository
import com.github.mustafaozhan.data.db.CurrencyDao
import com.github.mustafaozhan.data.db.OfflineRatesDao
import com.github.mustafaozhan.data.preferences.PreferencesRepository
import com.github.mustafaozhan.data.util.dateStringToFormattedString
import com.github.mustafaozhan.ui.main.MainData.Companion.DAY
import com.github.mustafaozhan.ui.main.model.AppTheme
import com.github.mustafaozhan.ui.main.settings.BackEffect
import com.github.mustafaozhan.ui.main.settings.ChangeThemeEffect
import com.github.mustafaozhan.ui.main.settings.CurrenciesEffect
import com.github.mustafaozhan.ui.main.settings.FeedBackEffect
import com.github.mustafaozhan.ui.main.settings.OnGitHubEffect
import com.github.mustafaozhan.ui.main.settings.RemoveAdsEffect
import com.github.mustafaozhan.ui.main.settings.SettingsViewModel
import com.github.mustafaozhan.ui.main.settings.ShareEffect
import com.github.mustafaozhan.ui.main.settings.SupportUsEffect
import com.github.mustafaozhan.ui.main.settings.ThemeDialogEffect
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class SettingsViewModelTest : BaseViewModelTest<SettingsViewModel>() {

    override lateinit var viewModel: SettingsViewModel

    @MockK
    lateinit var apiRepository: ApiRepository

    @RelaxedMockK
    lateinit var preferencesRepository: PreferencesRepository

    @RelaxedMockK
    lateinit var currencyDao: CurrencyDao

    @MockK
    lateinit var offlineRatesDao: OfflineRatesDao

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = SettingsViewModel(
            preferencesRepository,
            apiRepository,
            currencyDao,
            offlineRatesDao
        )
    }

    @Test
    fun `update theme`() = with(viewModel) {
        val appTheme = AppTheme.DARK
        updateTheme(appTheme)
        assertEquals(appTheme, state.appThemeType.value)
        assertEquals(ChangeThemeEffect(appTheme.themeValue), effect.value)
    }

    @Test
    fun `update ad expiration date`() = with(viewModel) {
        updateAddFreeDate()
        assertEquals(
            state.addFreeDate.value,
            Date(System.currentTimeMillis() + DAY).dateStringToFormattedString()
        )
    }

    // Event
    @Test
    fun `back click`() = with(viewModel) {
        getEvent().onBackClick()
        assertEquals(BackEffect, effect.value)
    }

    @Test
    fun `currencies click`() = with(viewModel) {
        getEvent().onCurrenciesClick()
        assertEquals(CurrenciesEffect, effect.value)
    }

    @Test
    fun `feedback click`() = with(viewModel) {
        getEvent().onFeedBackClick()
        assertEquals(FeedBackEffect, effect.value)
    }

    @Test
    fun `share click`() = with(viewModel) {
        getEvent().onShareClick()
        assertEquals(ShareEffect, effect.value)
    }

    @Test
    fun `support us click`() = with(viewModel) {
        getEvent().onSupportUsClick()
        assertEquals(SupportUsEffect, effect.value)
    }

    @Test
    fun `on github click`() = with(viewModel) {
        getEvent().onOnGitHubClick()
        assertEquals(OnGitHubEffect, effect.value)
    }

    @Test
    fun `on remove ad click`() = with(viewModel) {
        getEvent().onRemoveAdsClick()
        assertEquals(RemoveAdsEffect, effect.value)
    }

    @Test
    fun `on theme click`() = with(viewModel) {
        getEvent().onThemeClick()
        assertEquals(ThemeDialogEffect, effect.value)
    }
}
