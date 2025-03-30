package com.perrigogames.life4.android.feature.trial

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.perrigogames.life4.feature.trials.view.UITrialBottomSheet
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongEntryBottomSheet(
    viewData: UITrialBottomSheet.Details,
    bottomSheetState: SheetState,
    onAction: (TrialSessionAction) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { onDismiss() }
    ) {
        val decodedBitmap = remember(viewData.imagePath) {
            if (viewData.imagePath.isEmpty()) return@remember null
            Uri.parse(viewData.imagePath)
                ?.let { uri ->
                    println(uri.toString())
                    context.contentResolver.openInputStream(uri)
                }
                ?.use { BitmapFactory.decodeStream(it) }
        }
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            decodedBitmap?.let {
                Image(
                    bitmap = decodedBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            SongEntryContent(
                fields = viewData.fields,
                shortcuts = viewData.shortcuts,
                onAction = onAction,
            )
        }
    }

    LaunchedEffect(Unit) {
        bottomSheetState.expand()
    }
}

@Composable
fun SongEntryContent(
    fields: List<UITrialBottomSheet.Field>,
    shortcuts: List<UITrialBottomSheet.Shortcut>,
    modifier: Modifier = Modifier,
    onAction: (TrialSessionAction) -> Unit,
) {
    val context = LocalContext.current
    Row(modifier = modifier) {
        fields.forEach { field ->
            TextField(
                value = field.text,
                onValueChange = { newText ->
                    onAction(TrialSessionAction.ChangeText(field.id, newText))
                },
                enabled = field.enabled,
                placeholder = {
                    Text(field.placeholder.toString(context))
                },
                modifier = Modifier.weight(field.weight),
            )
            // FIXME error state
        }

        if (shortcuts != null) {
            // FIXME implement shortcuts
        }

    }
}
