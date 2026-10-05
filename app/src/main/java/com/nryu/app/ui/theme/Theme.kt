package com.nryu.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

val DarkBg = Color(0xFF121212)
val AccentColor = Color(0xFFBB86FC)
val GlassColor = Color(0x33FFFFFF)
val NeumorphicShadow = Color(0x4D000000)
val NeumorphicHighlight = Color(0x4DFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = AccentColor,
    background = DarkBg,
    surface = DarkBg,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun NryuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
