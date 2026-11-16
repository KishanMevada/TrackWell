package com.trackwell.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalDesignScale = compositionLocalOf { 1f }

val Number.ds: Dp
    @Composable
    get() = (toFloat() * LocalDesignScale.current).dp

val Number.dsp: TextUnit
    @Composable
    get() = (toFloat() * LocalDesignScale.current).sp
