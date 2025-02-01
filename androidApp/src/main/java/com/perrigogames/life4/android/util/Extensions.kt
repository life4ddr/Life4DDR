package com.perrigogames.life4.android.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SizedSpacer(size: Dp) = Spacer(modifier = Modifier.size(size))