package com.brendigo.sublix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.brendigo.sublix.notifications.ReminderScheduler
import com.brendigo.sublix.preview.PreviewSeed
import com.brendigo.sublix.BuildConfig
import com.brendigo.sublix.ui.SubliXApp
import com.brendigo.sublix.ui.SubliXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16,18,29)
        window.navigationBarColor = android.graphics.Color.rgb(16,18,29)
        ReminderScheduler.initialize(this)
        if (BuildConfig.DEBUG) PreviewSeed.prepare(this, intent)
        val previewScreen = if (BuildConfig.DEBUG) intent.getStringExtra("capture_screen") else null
        setContent { SubliXTheme { SubliXApp(startScreen = previewScreen) } }
    }
}
