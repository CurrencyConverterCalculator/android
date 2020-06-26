/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package mustafaozhan.github.com.ui.splash

import android.content.Intent
import android.os.Bundle
import com.github.mustafaozhan.basemob.view.activity.BaseActivity
import mustafaozhan.github.com.data.preferences.PreferencesRepository
import mustafaozhan.github.com.ui.main.MainActivity
import mustafaozhan.github.com.ui.slider.SliderActivity
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesRepository.syncPreferences()

        startActivity(
            Intent(
                this,
                if (preferencesRepository.firstRun) {
                    SliderActivity::class.java
                } else {
                    MainActivity::class.java
                }
            )
        )

        finish()
    }
}