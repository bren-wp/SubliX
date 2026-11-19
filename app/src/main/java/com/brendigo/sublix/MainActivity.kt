package com.brendigo.sublix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import com.brendigo.sublix.notifications.ReminderScheduler
import com.brendigo.sublix.preview.PreviewSeed
import com.brendigo.sublix.BuildConfig
import com.brendigo.sublix.ui.SubliXApp
import com.brendigo.sublix.ui.SubliXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Explicit edge-to-edge on Android 8–16; Material3 Scaffold applies
        // system-bar/cutout insets to the shared Free and Pro navigation shell.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        ReminderScheduler.initialize(this)
        if (BuildConfig.DEBUG) PreviewSeed.prepare(this, intent)
        val previewScreen = if (BuildConfig.DEBUG) intent.getStringExtra("capture_screen") else null
        setContent { SubliXTheme { SubliXApp(startScreen = previewScreen) } }
    }
}
