package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Navigation tabs matching the app design:
 * Beranda | Tagihan | Meter | Notifikasi | Profil
 */
enum class SmartWaterTab {
    BERANDA,
    TAGIHAN,
    METER,
    NOTIFIKASI,
    PROFIL
}

/**
 * Consistent Smart Water Bottom Navigation Bar across all screens:
 * - 5 Tabs: Beranda, Tagihan, Meter, Notifikasi (with red notification badge), Profil
 * - Active tab features vibrant brand blue tint (Color(0xFF0066CC)), bold font, and
 *   horizontal rounded indicator pill underneath.
 * - Inactive tabs feature clean slate gray tint (Color(0xFF94A3B8)).
 * - White surface with soft elevation shadow.
 */
@Composable
fun SmartWaterBottomBar(
    selectedTab: SmartWaterTab,
    onNavigateToHome: () -> Unit = {},
    onNavigateToTagihan: () -> Unit = {},
    onNavigateToMeter: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    hasNotificationDot: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("smart_water_bottom_bar"),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Beranda
            SmartWaterBottomNavItem(
                title = "Beranda",
                isSelected = selectedTab == SmartWaterTab.BERANDA,
                hasDot = false,
                icon = Icons.Default.Home,
                onClick = onNavigateToHome,
                testTag = "nav_beranda"
            )

            // Tab 2: Tagihan
            SmartWaterBottomNavItem(
                title = "Tagihan",
                isSelected = selectedTab == SmartWaterTab.TAGIHAN,
                hasDot = false,
                icon = Icons.Default.ReceiptLong,
                onClick = onNavigateToTagihan,
                testTag = "nav_tagihan"
            )

            // Tab 3: Lacak Meter
            SmartWaterBottomNavItem(
                title = "Lacak Meter",
                isSelected = selectedTab == SmartWaterTab.METER,
                hasDot = false,
                icon = Icons.Default.CameraAlt,
                onClick = onNavigateToMeter,
                testTag = "nav_laporkan"
            )

            // Tab 4: Notifikasi
            SmartWaterBottomNavItem(
                title = "Notifikasi",
                isSelected = selectedTab == SmartWaterTab.NOTIFIKASI,
                hasDot = hasNotificationDot,
                icon = Icons.Default.Notifications,
                onClick = onNavigateToNotification,
                testTag = "nav_notifikasi"
            )

            // Tab 5: Profil
            SmartWaterBottomNavItem(
                title = "Profil",
                isSelected = selectedTab == SmartWaterTab.PROFIL,
                hasDot = false,
                icon = Icons.Default.Person,
                onClick = onNavigateToProfile,
                testTag = "nav_profil"
            )
        }
    }
}

@Composable
fun SmartWaterBottomNavItem(
    title: String,
    isSelected: Boolean,
    hasDot: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val activeBlue = Color(0xFF0066CC)
    val inactiveGray = Color(0xFF94A3B8)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) activeBlue else inactiveGray,
                modifier = Modifier.size(24.dp)
            )

            if (hasDot) {
                Box(
                    modifier = Modifier
                        .offset(x = 3.dp, y = (-2).dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeBlue else inactiveGray
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Blue horizontal indicator pill below text
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(activeBlue)
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}
