package com.kkdev.flashping.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kkdev.flashping.R

// 1. Define the Lexend Deca font family
// Make sure your font files in res/font are lowercase_with_underscores
val LexendDeca = FontFamily(
    Font(R.font.lexenddeca_thin, FontWeight.Thin),
    Font(R.font.lexenddeca_light, FontWeight.Light),
    Font(R.font.lexenddeca_regular, FontWeight.Normal),
    Font(R.font.lexenddeca_medium, FontWeight.Medium),
    Font(R.font.lexenddeca_semibold, FontWeight.SemiBold),
    Font(R.font.lexenddeca_bold, FontWeight.Bold),
    Font(R.font.lexenddeca_extrabold, FontWeight.ExtraBold),
    Font(R.font.lexenddeca_black, FontWeight.Black)
)

// 2. Replace the default Typography with our new AppTypography using Lexend Deca
val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* You can override other text styles here as needed */
)
