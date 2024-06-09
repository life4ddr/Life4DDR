package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorText(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: @Composable () -> String?,
) = Text(
    text = text() ?: "",
    modifier = modifier,
    color = MaterialTheme.colorScheme.error,
)