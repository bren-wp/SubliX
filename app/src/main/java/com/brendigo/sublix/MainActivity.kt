package com.brendigo.sublix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.brendigo.sublix.notifications.ReminderScheduler
import com.brendigo.sublix.ui.SubliXApp
import com.brendigo.sublix.ui.SubliXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16,18,29)
        window.navigationBarColor = android.graphics.Color.rgb(16,18,29)
        ReminderScheduler.initialize(this)
        setContent { SubliXTheme { SubliXApp() } }
    }
}
