package com.clothesmatcher.ui

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.clothesmatcher.ui.theme.ClothesMatcherTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val wearScale = remember { Animatable(1f) }
    val ueRotationX = remember { Animatable(0f) }
    val ueColor = remember { Animatable(Color(0xFF6200EE)) }
    
    val finalColor = MaterialTheme.colorScheme.primary
    val colors = listOf(
        Color(0xFF6200EE), // Purple
        Color(0xFF03DAC6), // Teal
        Color(0xFFFF0266), // Pink
        Color(0xFFF44336), // Red
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3)  // Blue
    )

    LaunchedEffect(Unit) {
        // 1. "ue" rotates vertically and changes colors
        val rotationJob = launch {
            ueRotationX.animateTo(
                targetValue = 1440f,
                animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
            )
        }
        
        val colorJob = launch {
            var colorIndex = 0
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 2000) {
                ueColor.animateTo(
                    colors[colorIndex % colors.size],
                    animationSpec = tween(durationMillis = 250)
                )
                colorIndex++
            }
        }

        // 2. Start "Wear" click even earlier (at 1.75s)
        delay(1750) 
        
        launch {
            // Smaller enlargement (1.15x instead of 1.2x)
            wearScale.animateTo(1.15f, animationSpec = tween(durationMillis = 75, easing = LinearOutSlowInEasing))
            wearScale.animateTo(1.0f, animationSpec = tween(durationMillis = 75, easing = FastOutLinearInEasing))
        }

        // Wait for rotation and colors to finish their 2s duration
        rotationJob.join()
        colorJob.cancel()

        // 3. Lock UE color to match WEAR
        ueColor.animateTo(finalColor, animationSpec = tween(durationMillis = 100))

        delay(1000)
        onAnimationFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "WEAR",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = finalColor,
                    modifier = Modifier.graphicsLayer {
                        scaleX = wearScale.value
                        scaleY = wearScale.value
                    }
                )
                Text(
                    text = "UE",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = ueColor.value,
                    modifier = Modifier.graphicsLayer {
                        rotationX = ueRotationX.value
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Phone")
@Composable
fun SplashScreenPreview() {
    ClothesMatcherTheme {
        SplashScreen(onAnimationFinished = {})
    }
}

@Preview(
    showBackground = true, 
    device = "spec:width=1280dp,height=720dp,dpi=240", 
    name = "Store Tablet 10 (16:9 Landscape)"
)
@Composable
fun SplashScreenStoreLandscapePreview() {
    ClothesMatcherTheme {
        SplashScreen(onAnimationFinished = {})
    }
}

@Preview(
    showBackground = true, 
    device = "spec:width=1440dp,height=2560dp,dpi=240",
    name = "Store Tablet 10 (9:16 Portrait)"
)
@Composable
fun SplashScreenStorePortraitPreview() {
    ClothesMatcherTheme {
        SplashScreen(onAnimationFinished = {})
    }
}
