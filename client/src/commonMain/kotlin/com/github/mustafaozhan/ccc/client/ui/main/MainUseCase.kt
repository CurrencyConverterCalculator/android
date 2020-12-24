/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.ccc.client.ui.main

import com.github.mustafaozhan.ccc.client.base.BaseUseCase
import com.github.mustafaozhan.ccc.client.util.isRewardExpired
import com.github.mustafaozhan.ccc.client.util.isWeekPassed
import com.github.mustafaozhan.ccc.common.settings.SettingsRepository
import kotlinx.datetime.Clock

class MainUseCase(private val settingsRepository: SettingsRepository) : BaseUseCase() {

    fun shouldShowReview() = settingsRepository.lastReviewRequest.isWeekPassed()

    fun setLastReview() {
        settingsRepository.lastReviewRequest = Clock.System.now().toEpochMilliseconds()
    }

    fun isFistRun() = settingsRepository.firstRun

    fun getAppTheme() = settingsRepository.appTheme

    fun isRewardExpired() = settingsRepository.adFreeActivatedDate.isRewardExpired()
}
