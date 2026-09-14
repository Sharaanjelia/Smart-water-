package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Slide 02 Illustration:
 * Hands holding smartphone capturing the water meter, with lush foliage background.
 */
@Composable
fun OnboardingPhotoMeterIllustration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background Foliage & ambient soft aura
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft radial aura behind phone
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE0F2FE), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.45f
                )
            )

            // Left Foliage/Leaves
            drawFoliageCluster(
                baseX = w * 0.22f,
                baseY = h * 0.65f,
                isLeft = true
            )

            // Right Foliage/Leaves
            drawFoliageCluster(
                baseX = w * 0.78f,
                baseY = h * 0.65f,
                isLeft = false
            )
        }

        // Phone Container with Hands
        Box(
            modifier = Modifier
                .width(170.dp)
                .height(260.dp),
            contentAlignment = Alignment.Center
        ) {
            // Hands holding the phone on left and right
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Left hand/thumb
                val leftHand = Path().apply {
                    moveTo(0f, h * 0.70f)
                    cubicTo(w * 0.08f, h * 0.66f, w * 0.16f, h * 0.62f, w * 0.18f, h * 0.52f)
                    cubicTo(w * 0.18f, h * 0.46f, w * 0.08f, h * 0.44f, w * 0.02f, h * 0.46f)
                    lineTo(0f, h * 0.70f)
                    close()
                }
                drawPath(
                    path = leftHand,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA)),
                        start = Offset(0f, h * 0.5f),
                        end = Offset(w * 0.2f, h * 0.6f)
                    )
                )

                // Right hand/thumb
                val rightHand = Path().apply {
                    moveTo(w, h * 0.70f)
                    cubicTo(w * 0.92f, h * 0.66f, w * 0.84f, h * 0.62f, w * 0.82f, h * 0.52f)
                    cubicTo(w * 0.82f, h * 0.46f, w * 0.92f, h * 0.44f, w * 0.98f, h * 0.46f)
                    lineTo(w, h * 0.70f)
                    close()
                }
                drawPath(
                    path = rightHand,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA)),
                        start = Offset(w, h * 0.5f),
                        end = Offset(w * 0.8f, h * 0.6f)
                    )
                )

                // Lower hands palms/wrists holding bottom
                drawRoundRect(
                    color = Color(0xFFFED7AA),
                    topLeft = Offset(w * 0.04f, h * 0.68f),
                    size = Size(w * 0.26f, h * 0.28f),
                    cornerRadius = CornerRadius(24f, 24f)
                )
                drawRoundRect(
                    color = Color(0xFFFED7AA),
                    topLeft = Offset(w * 0.70f, h * 0.68f),
                    size = Size(w * 0.26f, h * 0.28f),
                    cornerRadius = CornerRadius(24f, 24f)
                )
            }

            // Smartphone Frame
            Box(
                modifier = Modifier
                    .width(135.dp)
                    .height(240.dp)
                    .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color(0x33000000))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF1E293B))
                    .border(3.dp, Color(0xFF0F172A), RoundedCornerShape(26.dp))
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                // Phone Screen (Camera preview of water meter)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Camera speaker pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp)
                            .width(28.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF0F172A))
                    )

                    // Inside Camera: Water Meter
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Round Blue Meter Dial
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7))
                                .border(5.dp, Color(0xFF0369A1), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Inner dial face
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Brand text
                                    Text(
                                        text = "PDAM",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Register odometer box
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "000108",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            letterSpacing = 1.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    // Small spinner gear
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0284C7))
                                    )
                                }
                            }
                        }

                        // Bottom pipe extension
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(8.dp)
                                .background(Color(0xFF0284C7), RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Slide 03 Illustration:
 * Water meter dial with corner scanning viewfinder brackets [ ] representing AI-OCR.
 */
@Composable
fun OnboardingOCRIllustration(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ocrScan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient soft blue circular glow
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFE0F2FE).copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        // Viewfinder Frame with corner brackets
        Box(
            modifier = Modifier.size(230.dp),
            contentAlignment = Alignment.Center
        ) {
            // The Water Meter Graphic
            Box(
                modifier = Modifier
                    .size(175.dp)
                    .shadow(12.dp, CircleShape, spotColor = Color(0x330284C7))
                    .clip(CircleShape)
                    .background(Color(0xFF0284C7))
                    .border(8.dp, Color(0xFF0369A1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Dial Face
                Box(
                    modifier = Modifier
                        .size(142.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SMART METER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Large Odometer counter box
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "0 0 0 1",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Decimal digits in red badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFEF4444))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "0 8",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Spinner gear & flow indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7))
                                    .border(2.dp, Color(0xFF38BDF8), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "m³",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Left & right pipe attachments
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                // Left pipe
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(28.dp)
                        .height(22.dp)
                        .background(Color(0xFF0284C7), RoundedCornerShape(4.dp))
                )
                // Right pipe
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(28.dp)
                        .height(22.dp)
                        .background(Color(0xFF0284C7), RoundedCornerShape(4.dp))
                )
            }

            // AI-OCR Viewfinder Bracket Corners [ ] and Animated Scanning line
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val bracketLen = 32f
                val strokeW = 8f
                val cornerColor = Color(0xFF0284C7)
                val pad = 12f

                // Top-Left Corner Bracket
                drawLine(
                    color = cornerColor,
                    start = Offset(pad, pad + bracketLen),
                    end = Offset(pad, pad),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(pad, pad),
                    end = Offset(pad + bracketLen, pad),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )

                // Top-Right Corner Bracket
                drawLine(
                    color = cornerColor,
                    start = Offset(w - pad - bracketLen, pad),
                    end = Offset(w - pad, pad),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(w - pad, pad),
                    end = Offset(w - pad, pad + bracketLen),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )

                // Bottom-Left Corner Bracket
                drawLine(
                    color = cornerColor,
                    start = Offset(pad, h - pad - bracketLen),
                    end = Offset(pad, h - pad),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(pad, h - pad),
                    end = Offset(pad + bracketLen, h - pad),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )

                // Bottom-Right Corner Bracket
                drawLine(
                    color = cornerColor,
                    start = Offset(w - pad - bracketLen, h - pad),
                    end = Offset(w - pad, h - pad),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = cornerColor,
                    start = Offset(w - pad, h - pad - bracketLen),
                    end = Offset(w - pad, h - pad),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )

                // Laser scan line across viewfinder
                val curY = h * scanLineY
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF38BDF8),
                            Color(0xFF0077B6),
                            Color(0xFF38BDF8),
                            Color.Transparent
                        )
                    ),
                    start = Offset(pad * 2, curY),
                    end = Offset(w - pad * 2, curY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Slide 04 Illustration:
 * Bill/invoice receipt card displaying "TAGIHAN", "Rp 150.000", with green checkmark circle badge.
 */
@Composable
fun OnboardingBillIllustration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Soft blue background decorative aura & plant accents
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background soft blue glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE0F2FE), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.45f
                )
            )

            // Leaf accent behind card on left
            drawLeafAccent(
                tipX = w * 0.20f,
                tipY = h * 0.60f,
                baseX = w * 0.32f,
                baseY = h * 0.72f,
                color = Color(0xFF0284C7).copy(alpha = 0.5f)
            )
        }

        // Bill Document Card Stack
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(240.dp)
        ) {
            // White Bill Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color(0x220284C7))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header: TAGIHAN
                    Text(
                        text = "TAGIHAN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )

                    // Skeleton line representations of bill lines
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFE2E8F0))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFE2E8F0))
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount
                        Text(
                            text = "Rp 150.000",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Droplet badge button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.40f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFCBD5E1))
                        )
                    }
                }
            }

            // Green Checkmark Circular Badge at bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
                    .size(46.dp)
                    .shadow(8.dp, CircleShape, spotColor = Color(0x4422C55E))
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E))
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// Helpers for foliage drawings
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFoliageCluster(
    baseX: Float,
    baseY: Float,
    isLeft: Boolean
) {
    val dir = if (isLeft) -1f else 1f
    // Leaf 1
    drawLeafAccent(
        baseX = baseX,
        baseY = baseY,
        tipX = baseX + dir * 55f,
        tipY = baseY - 50f,
        color = Color(0xFF10B981)
    )
    // Leaf 2
    drawLeafAccent(
        baseX = baseX,
        baseY = baseY + 20f,
        tipX = baseX + dir * 65f,
        tipY = baseY + 10f,
        color = Color(0xFF059669)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeafAccent(
    baseX: Float,
    baseY: Float,
    tipX: Float,
    tipY: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(baseX, baseY)
        quadraticBezierTo((baseX + tipX) / 2 - 15f, (baseY + tipY) / 2 + 10f, tipX, tipY)
        quadraticBezierTo((baseX + tipX) / 2 + 15f, (baseY + tipY) / 2 - 10f, baseX, baseY)
        close()
    }
    drawPath(path = path, color = color, style = Fill)
}
