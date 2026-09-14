package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SmartWaterSplashLogo
import com.example.ui.components.SplashWaterWaveBackground
import kotlinx.coroutines.delay

/**
 * Screen 01 - Splash:
 * - Water droplet logo (vibrant blue droplet with inner green leaf curve)
 * - "Smart Water" title ("Smart" in rich blue, "Water" in fresh green)
 * - "Smart Self Meter Reading" subtitle
 * - Tagline: "Kelola meter air dan tagihan Anda dengan mudah."
 * - Flowing multi-layer blue water waves at bottom with green plant sprout on right
 * - Auto-advances or taps to navigate to Onboarding
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Automatically transition to Onboarding after 2.4 seconds
    LaunchedEffect(Unit) {
        delay(2400)
        onNavigateToHome()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onNavigateToHome()
            }
            .testTag("splash_screen_container")
    ) {
        // Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.7f))

            // Water Droplet Logo (Blue body + Green leaf curve)
            SmartWaterSplashLogo(
                size = 115.dp,
                modifier = Modifier.testTag("splash_water_logo")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title: "Smart Water"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Smart ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0066CC),
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("smart_text_brand")
                )
                Text(
                    text = "Water",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("water_text_brand")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle: "Smart Self Meter Reading"
            Text(
                text = "Smart Self Meter Reading",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                letterSpacing = 0.2.sp,
                modifier = Modifier.testTag("smart_water_subtitle")
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Tagline: "Kelola meter air dan tagihan Anda dengan mudah."
            Text(
                text = "Kelola meter air dan\ntagihan Anda dengan mudah.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF475569),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.testTag("splash_tagline")
            )

            Spacer(modifier = Modifier.weight(1.3f))
        }

        // Bottom Decorative Water Waves with Green Leaves
        SplashWaterWaveBackground(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .testTag("splash_wave_bottom"),
            height = 200.dp
        )
    }
}

