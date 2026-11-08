package com.eastercalculator.app.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

private data class EmojiParticle(
    val initialXRatio: Float,
    val initialYOffset: Float,
    val speed: Float,
    val sizePx: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val rotationSpeed: Float,
    val companionEmoji: String
)

@Composable
fun EmojiRainCanvas(
    emoji: String,
    particleCount: Int = 55,
    durationMs: Int = 4500,
    onFinished: () -> Unit = {}
) {
    val density = LocalDensity.current
    val progress = remember { Animatable(0f) }

    val particles = remember(emoji) {
        val companionList = listOf(emoji, emoji, emoji, "✨", "🎉", "⭐")
        List(particleCount) {
            EmojiParticle(
                initialXRatio = Random.nextFloat(),
                initialYOffset = -Random.nextFloat() * 800f - 50f,
                speed = Random.nextFloat() * 1.2f + 0.8f,
                sizePx = with(density) { Random.nextInt(28, 52).dp.toPx() },
                swayAmplitude = Random.nextFloat() * 40f + 15f,
                swayFrequency = Random.nextFloat() * 4f + 2f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                companionEmoji = companionList.random()
            )
        }
    }

    LaunchedEffect(emoji) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
        onFinished()
    }

    val textPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onFinished()
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val t = progress.value

            particles.forEach { p ->
                val totalDistance = height + 1000f
                val currentY = p.initialYOffset + (t * totalDistance * p.speed)

                if (currentY in -100f..(height + 100f)) {
                    val sway = sin(t * Math.PI.toFloat() * p.swayFrequency) * p.swayAmplitude
                    val currentX = (p.initialXRatio * width + sway).coerceIn(20f, width - 20f)

                    val alphaRatio = when {
                        t > 0.85f -> (1f - (t - 0.85f) / 0.15f).coerceIn(0f, 1f)
                        currentY > height - 150f -> ((height - currentY) / 150f).coerceIn(0f, 1f)
                        else -> 1f
                    }

                    textPaint.textSize = p.sizePx
                    textPaint.alpha = (alphaRatio * 255).toInt()

                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.rotate(
                        p.rotationSpeed * t,
                        currentX,
                        currentY
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        p.companionEmoji,
                        currentX,
                        currentY,
                        textPaint
                    )
                    drawContext.canvas.nativeCanvas.restore()
                }
            }
        }
    }
}
