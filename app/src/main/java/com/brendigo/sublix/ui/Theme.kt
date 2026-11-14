package com.brendigo.sublix.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.brendigo.sublix.BuildConfig

val Background = Color(0xFF10121D)
val Surface = Color(0xFF1D2030)
val Raised = Color(0xFF24263C)
val Accent = Color(0xFFA99AFF)
val Muted = Color(0xFFA7AAC0)
private val Purple = Color(0xFF7866F5)
private val Gold = Color(0xFFE8BA7D)

@Composable
fun SubliXTheme(content: @Composable () -> Unit) {
    val pro=BuildConfig.FLAVOR=="pro"
    MaterialTheme(colorScheme=darkColorScheme(
        primary=if(pro) Gold else Accent, onPrimary=Color(0xFF171527),
        secondary=if(pro) Gold else Purple,
        background=Background, onBackground=Color(0xFFF3F2FF),
        surface=Surface, onSurface=Color(0xFFF3F2FF),
        surfaceVariant=Raised, onSurfaceVariant=Muted,
        outline=Color(0xFF575A75), error=Color(0xFFFF9AA5)
    ), content=content)
}
