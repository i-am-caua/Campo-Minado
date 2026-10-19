package com.example.campominado.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun CampoMinadoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CampoMinadoColors,
        typography = Typography,
        content = content
    )
}
