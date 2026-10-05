package com.nryu.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.nryu.app.ui.theme.AccentColor
import com.nryu.app.ui.theme.DarkBg
import com.nryu.app.ui.theme.GlassColor

@Composable
fun VisualizerCore(amplitude: FloatArray) {
    val transition = remember { Animatable(0f) }
    
    LaunchedEffect(amplitude) {
        if (amplitude.isNotEmpty()) {
            val avg = amplitude.average().toFloat()
            transition.animateTo(avg, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = Modifier
            .size(300.dp)
            .clip(CircleShape)
            .background(GlassColor)
            .drawBehind {
                drawCircle(
                    color = AccentColor,
                    radius = (size.minDimension / 2) * (1f + transition.value * 0.2f),
                    alpha = 0.3f
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            
            if (amplitude.isNotEmpty()) {
                val step = (2 * Math.PI).toFloat() / amplitude.size
                amplitude.forEachIndexed { index, value ->
                    val angle = index * step
                    val x = center.x + Math.cos(angle.toDouble()).toFloat() * radius
                    val y = center.y + Math.sin(angle.toDouble()).toFloat() * radius
                    
                    val lineLength = 20f + (value * 50f)
                    val lx = center.x + Math.cos(angle.toDouble()).toFloat() * (radius + lineLength)
                    val ly = center.y + Math.sin(angle.toDouble()).toFloat() * (radius + lineLength)
                    
                    drawLine(
                        color = AccentColor,
                        start = Offset(x, y),
                        end = Offset(lx, ly),
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}

private fun Offset(x: Float, y: Float) = androidx.compose.ui.geometry.Offset(x, y)
