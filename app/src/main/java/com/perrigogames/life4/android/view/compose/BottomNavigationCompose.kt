package com.perrigogames.life4.android.ui

import android.widget.Button
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4DDRTheme

@Composable
fun BottomNavigationButtons(
    leftText: String,
    rightText: String,
    onLeftButtonClicked: () -> Unit,
    onRightButtonClicked: () -> Unit,
) {
    Row {
        AndroidView(
            factory = { Button(it, null, R.style.BottomProgressButtonRightBorder).apply {
                text = leftText
                setOnClickListener { onLeftButtonClicked() }
            } },
        )
        AndroidView(
            factory = { Button(it, null, R.style.BottomProgressButtonLeftBorder).apply {
                text = rightText
                setOnClickListener { onRightButtonClicked() }
            } },
        )
        // TODO button backgrounds in Compose
//        Button(
//            onClick = onLeftButtonClicked,
//            shape = Shapes.large,
//            modifier = Modifier.weight(0.5f)
//        ) {
//            Text(text = leftText)
//        }
//        Button(
//            onClick = onRightButtonClicked,
//            shape = Shapes.large,
//            modifier = Modifier.weight(0.5f)
//        ) {
//            Text(text = rightText)
//        }
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