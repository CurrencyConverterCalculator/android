/*
 * Copyright (c) 2021 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.ccc.client.viewmodel.main

import com.github.mustafaozhan.ccc.client.base.BaseSEEDViewModel
import com.github.mustafaozhan.ccc.client.base.BaseState
import com.github.mustafaozhan.ccc.client.util.isRewardExpired
import com.github.mustafaozhan.ccc.client.util.isWeekPassed
import com.github.mustafaozhan.ccc.client.viewmodel.main.MainData.Companion.AD_DELAY_INITIAL
import com.github.mustafaozhan.ccc.client.viewmodel.main.MainData.Companion.AD_DELAY_NORMAL
import com.github.mustafaozhan.ccc.client.viewmodel.main.MainData.Companion.REVIEW_DELAY
import com.github.mustafaozhan.ccc.common.settings.SettingsRepository
import com.github.mustafaozhan.ccc.common.util.nowAsLong
import com.github.mustafaozhan.logmob.kermit
import com.github.mustafaozhan.scopemob.whether
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsRepository: SettingsRepository
) : BaseSEEDViewModel(), MainEvent {
    // region SEED
    override val state: StateFlow<BaseState>? = null

    private val _effect = MutableSharedFlow<MainEffect>()
    override val effect = _effect.asSharedFlow()

    override val event = this as MainEvent

    override val data = MainData()
    // endregion

    init {
        kermit.d { "MainViewModel init" }
    }

    private fun setupInterstitialAdTimer() {
        data.adVisibility = true

        data.adJob = clientScope.launch {
            delay(getAdDelay())

            while (isActive && !isFistRun()) {
                if (data.adVisibility && settingsRepository.adFreeEndDate.isRewardExpired()) {
                    _effect.emit(MainEffect.ShowInterstitialAd)
                    data.isInitialAd = false
                }
                delay(getAdDelay())
            }
        }
    }

    private fun getAdDelay() = if (data.isInitialAd) AD_DELAY_INITIAL else AD_DELAY_NORMAL

    fun isFistRun() = settingsRepository.firstRun

    fun getAppTheme() = settingsRepository.appTheme

    fun checkReview() = clientScope
        .whether { settingsRepository.lastReviewRequest.isWeekPassed() }
        ?.launch {
            delay(REVIEW_DELAY)
            _effect.emit(MainEffect.RequestReview)
            settingsRepository.lastReviewRequest = nowAsLong()
        }

    override fun onCleared() {
        kermit.d { "MainViewModel onCleared" }
        super.onCleared()
    }

    // region Event
    override fun onPause() = with(data) {
        kermit.d { "MainViewModel onPause" }
        adJob.cancel()
        adVisibility = false
    }

    override fun onResume() {
        kermit.d { "MainViewModel onResume" }
        setupInterstitialAdTimer()
    }
    // endregion
}
