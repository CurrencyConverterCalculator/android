/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package mustafaozhan.github.com.mycurrencies.ui.main.settings.model

import com.github.mustafaozhan.basemob.model.BaseEffect

sealed class SettingsEffect : BaseEffect()

object FewCurrencyEffect : SettingsEffect()

object CalculatorEffect : SettingsEffect()
