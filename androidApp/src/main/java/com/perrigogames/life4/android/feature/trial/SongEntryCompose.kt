package com.perrigogames.life4.android.feature.trial

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.util.InteractiveImage
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
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { onDismiss() }
    ) {
        SongEntryBottomSheetContent(viewData, onAction)
    }

    LaunchedEffect(Unit) {
        bottomSheetState.expand()
    }
}

@Composable
fun SongEntryBottomSheetContent(
    viewData: UITrialBottomSheet.Details,
    onAction: (TrialSessionAction) -> Unit,
) {
    BackHandler {
        onAction(TrialSessionAction.HideBottomSheet)
    }

    val context = LocalContext.current
    val decodedBitmap = remember(viewData.imagePath) {
        if (viewData.imagePath.isEmpty()) return@remember null
        try {
            Uri.parse(viewData.imagePath)
                ?.let { uri ->
                    println(uri.toString())
                    context.contentResolver.openInputStream(uri)
                }
                ?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        decodedBitmap?.let {
            InteractiveImage(
                bitmap = decodedBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        SongEntryControls(
            fields = viewData.fields,
            shortcuts = viewData.shortcuts,
            isEdit = viewData.isEdit,
            submitAction = viewData.onDismissAction,
            onAction = onAction,
        )
    }
}

@Composable
fun SongEntryControls(
    fields: List<List<UITrialBottomSheet.Field>>,
    shortcuts: List<UITrialBottomSheet.Shortcut>,
    isEdit: Boolean,
    submitAction: TrialSessionAction,
    modifier: Modifier = Modifier,
    onAction: (TrialSessionAction) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val flatFields = remember(fields) { fields.flatten() }
    val focusRequesters = remember(flatFields.size) { List(flatFields.size) { FocusRequester() } }
    
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fields.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { field ->
                        var value by remember { mutableStateOf(TextFieldValue(field.text)) }
                        val isLast = field == row.last() && row == fields.last()

                        LaunchedEffect(field.text) {
                            if (value.text != field.text) {
                                value = TextFieldValue(field.text)
                            }
                        }

                        TextField(
                            value = value,
                            onValueChange = { newText: TextFieldValue ->
                                value = newText
                                onAction(TrialSessionAction.ChangeText(field.id, newText.text.toString()))
                            },
                            enabled = field.enabled,
                            label = {
                                Text(field.label.toString(context))
                            },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = if (isLast) ImeAction.Done else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    val nextIndex = flatFields.indexOf(field) + 1
                                    if (nextIndex < focusRequesters.size) {
                                        focusRequesters[nextIndex].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                },
                                onDone = {
                                    onAction(submitAction)
                                }
                            ),
                            modifier = Modifier
                                .weight(field.weight)
                                .focusRequester(focusRequesters[flatFields.indexOf(field)])
                        )
                        // FIXME error state
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        if (shortcuts.isNotEmpty()) {
            // FIXME implement shortcuts
        }

        IconButton(
            onClick = { onAction(submitAction) },
        ) {
            Icon(
                imageVector = if (isEdit) {
                    Icons.Filled.Done
                } else {
                    Icons.AutoMirrored.Filled.ArrowForward
                },
                contentDescription = "Next"
            )
        }
    }
}
