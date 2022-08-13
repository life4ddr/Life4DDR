package com.perrigogames.life4.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.perrigogames.life4.android.compose.LIFE4DDRTheme
import com.perrigogames.life4.android.compose.Shapes

@Composable
fun BottomNavigationButtons(
    leftText: String,
    rightText: String,
    onLeftButtonClicked: () -> Unit,
    onRightButtonClicked: () -> Unit,
) {
    Row {
        Button(
            onClick = onLeftButtonClicked,
            shape = Shapes.large,
            modifier = Modifier.weight(0.5f)
        ) {
            Text(text = leftText)
        }
        Button(
            onClick = onRightButtonClicked,
            shape = Shapes.large,
            modifier = Modifier.weight(0.5f)
        ) {
            Text(text = rightText)
        }
    }
}

@Composable
@Preview
fun BottomNavigationButtonsPreview() {
    LIFE4DDRTheme {
        BottomNavigationButtons(
            leftText = "Cancel",
            rightText = "Continue",
            onLeftButtonClicked = {},
            onRightButtonClicked = {},
        )
    }
}