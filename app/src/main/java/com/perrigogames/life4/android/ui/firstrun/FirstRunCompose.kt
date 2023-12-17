@file:OptIn(ExperimentalAnimationApi::class)

package com.perrigogames.life4.android.ui.firstrun

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.primaryButtonColors
import com.perrigogames.life4.android.view.compose.ErrorText
import com.perrigogames.life4.data.SocialNetwork
import com.perrigogames.life4.model.settings.InitState
import com.perrigogames.life4.viewmodel.FirstRunError.RivalCodeError
import com.perrigogames.life4.viewmodel.FirstRunError.UsernameError
import com.perrigogames.life4.viewmodel.FirstRunInfoViewModel
import com.perrigogames.life4.viewmodel.FirstRunPath
import com.perrigogames.life4.viewmodel.FirstRunStep
import com.perrigogames.life4.viewmodel.FirstRunStep.Landing
import com.perrigogames.life4.viewmodel.FirstRunStep.PathStep.Completed
import com.perrigogames.life4.viewmodel.FirstRunStep.PathStep.InitialRankSelection
import com.perrigogames.life4.viewmodel.FirstRunStep.PathStep.RivalCode
import com.perrigogames.life4.viewmodel.FirstRunStep.PathStep.SocialHandles
import com.perrigogames.life4.viewmodel.FirstRunStep.PathStep.Username
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun FirstRunScreen(
    viewModel: FirstRunInfoViewModel = viewModel(
        factory = createViewModelFactory { FirstRunInfoViewModel() }
    ),
    onComplete: (InitState) -> Unit,
    onClose: () -> Unit,
) {
    val step: FirstRunStep by viewModel.state.collectAsState(Landing)

    BackHandler {
        if (!viewModel.navigateBack()) {
            onClose()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(0.75f)
                .fillMaxHeight()
        ) {
            FirstRunHeader(
                showWelcome = step == Landing,
                modifier = Modifier.fillMaxWidth()
            )

            val contentModifier = Modifier.fillMaxWidth()

            AnimatedContent(
                targetState = step,
                label = "First run content transitions",
            ) { step ->
                when (step) {
                    Landing -> {
                        FirstRunNewUser(modifier = contentModifier) {
                            viewModel.newUserSelected(it)
                        }
                    }
                    is Username -> {
                        FirstRunUsername(
                            viewModel = viewModel,
                            step = step,
                            modifier = contentModifier,
                        )
                    }
                    is RivalCode -> {
                        FirstRunRivalCode(
                            viewModel = viewModel,
                            modifier = contentModifier,
                        )
                    }
                    is SocialHandles -> {
                        FirstRunSocials(
                            viewModel = viewModel,
                            modifier = contentModifier,
                        )
                    }
                    is InitialRankSelection -> {
                        FirstRunRankMethod(
                            step = step,
                            modifier = contentModifier,
                            onRankMethodSelected = viewModel::rankMethodSelected
                        )
                    }
                    is Completed -> { onComplete(step.rankSelection) }
                    else -> error("Unsupported step $step")
                }
            }
        }

        if (step.showNextButton) {
            Button(
                onClick = { viewModel.navigateNext() },
                content = { Text("Next") },
                colors = primaryButtonColors(),
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun FirstRunHeader(
    showWelcome: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = showWelcome,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = stringResource(MR.strings.first_run_landing_header),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        Image(
            painter = painterResource(R.drawable.life4_logo_invert),
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            contentScale = ContentScale.Fit,
            contentDescription = null,
        )
    }
}

@Composable
fun FirstRunNewUser(
    modifier: Modifier = Modifier,
    onNewUserSelected: (Boolean) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(MR.strings.first_run_landing_description),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        ) {
            Button(
                onClick = { onNewUserSelected(true) },
                colors = primaryButtonColors(),
                content = { Text(
                    text = stringResource(MR.strings.yes),
                ) },
                modifier = Modifier.weight(1f, false)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Button(
                onClick = { onNewUserSelected(false) },
                colors = primaryButtonColors(),
                content = { Text(
                    text = stringResource(MR.strings.no),
                ) },
                modifier = Modifier.weight(1f, false)
            )
        }
    }
}

@Composable
fun FirstRunUsername(
    modifier: Modifier = Modifier,
    step: Username,
    viewModel: FirstRunInfoViewModel = viewModel(
        factory = createViewModelFactory { FirstRunInfoViewModel() }
    ),
) {
    val username: String by viewModel.username.collectAsState()
    val error: UsernameError? by viewModel.errorOfType<UsernameError>().collectAsState(null)
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Text(
            text = step.headerText.toString(LocalContext.current),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(
            modifier = Modifier.size(16.dp)
        )
        step.descriptionText?.let { description ->
            Text(
                text = description.toString(LocalContext.current),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(
                modifier = Modifier.size(16.dp)
            )
        }
        OutlinedTextField(
            value = username,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
            label = { Text(
                text = stringResource(MR.strings.username),
            ) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() },
            ),
            supportingText = {
                AnimatedVisibility(visible = error != null) {
                    ErrorText { error?.errorText?.toString(LocalContext.current) }
                }
            },
            onValueChange = { text: String -> viewModel.username.value = text },
            modifier = Modifier.onKeyEvent {
                return@onKeyEvent if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    focusManager.clearFocus()
                    true
                } else false
            }
                .fillMaxWidth()
        )
    }
}

@Composable
fun FirstRunRivalCode(
    modifier: Modifier = Modifier,
    viewModel: FirstRunInfoViewModel = viewModel(
        factory = createViewModelFactory { FirstRunInfoViewModel() }
    ),
) {
    val rivalCode: String by viewModel.rivalCode.collectAsState()
    val error: RivalCodeError? by viewModel.errorOfType<RivalCodeError>().collectAsState(null)

    Column(modifier = modifier) {
        Text(
            text = stringResource(MR.strings.first_run_rival_code_header),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(MR.strings.first_run_rival_code_description_1),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(MR.strings.first_run_rival_code_description_2),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
        )
        RivalCodeEntry(
            rivalCode = rivalCode,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp, bottom = 8.dp),
        ) { viewModel.rivalCode.value = it }
        AnimatedVisibility(visible = error != null) {
            ErrorText { error?.errorText?.toString(LocalContext.current) }
        }
    }
}

@Composable
fun RivalCodeEntry(
    rivalCode: String,
    modifier: Modifier = Modifier,
    onTextChanged: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = rivalCode,
        onValueChange = {
            if (it.length <= 8) {
                onTextChanged(it)
                if (it.length == 8) {
                    focusManager.clearFocus()
                }
            }
        },
        textStyle = MaterialTheme.typography.labelMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        decorationBox = {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                @Composable
                fun Cell(text: String) {
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.75f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(8.dp)
                            ),
                    )
                }

                repeat(4) { idx ->
                    val char = when {
                        idx >= rivalCode.length -> ""
                        else -> rivalCode[idx].toString()
                    }
                    Cell(char)
                    Spacer(modifier = Modifier.size(6.dp))
                }
                Text(
                    text = "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium,
                )
                repeat(4) { idx ->
                    val char = when {
                        idx + 4 >= rivalCode.length -> ""
                        else -> rivalCode[idx + 4].toString()
                    }
                    Spacer(modifier = Modifier.size(6.dp))
                    Cell(char)
                }
            }
        }
    )
}

@Composable
fun FirstRunSocials(
    modifier: Modifier = Modifier,
    viewModel: FirstRunInfoViewModel = viewModel(
        factory = createViewModelFactory { FirstRunInfoViewModel() }
    ),
) {
    val socials: Map<SocialNetwork, String> by viewModel.socialNetworks.collectAsState()

    Column(modifier = modifier) {
        Text(
            text = stringResource(MR.strings.first_run_social_header),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(MR.strings.first_run_social_description),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
        )
        LazyColumn(
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            item {
                Button(
                    onClick = {},
                    content = {
                        Text(
                            text = stringResource(MR.strings.first_run_social_add_new),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
            items(socials.toList()) { (network, name) ->
                Row {
                    Text(
                        text = "$network: ",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = name,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
fun FirstRunRankMethod(
    step: InitialRankSelection,
    modifier: Modifier = Modifier,
    onRankMethodSelected: (InitState) -> Unit = {},
) {
    Column(modifier = modifier) {
        @Composable
        fun OptionButton(
            method: InitState,
        ) {
            Button(
                onClick = { onRankMethodSelected(method) },
                colors = primaryButtonColors(),
                content = { Text(
                    text = method.description.toString(LocalContext.current),
                    textAlign = TextAlign.Center,
                ) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Text(
            text = stringResource(MR.strings.first_run_rank_selection_header),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.size(16.dp))

        step.path.allowedRankSelectionTypes().forEach {
            OptionButton(it)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(MR.strings.first_run_rank_selection_footer),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunHeaderPreview() {
    LIFE4Theme {
        FirstRunHeader(true)
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunNewUserPreview() {
    LIFE4Theme {
        FirstRunNewUser {}
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunUsernameNewPreview() {
    LIFE4Theme {
        FirstRunUsername(step = Username(FirstRunPath.NEW_USER_LOCAL))
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunUsernameExistingPreview() {
    LIFE4Theme {
        FirstRunUsername(step = Username(FirstRunPath.EXISTING_USER_LOCAL))
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunRivalCodePreview() {
    LIFE4Theme {
        FirstRunRivalCode()
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunRivalCodeEntryPreview() {
    LIFE4Theme {
        RivalCodeEntry("12345678")
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunSocialsPreview() {
    LIFE4Theme {
        FirstRunSocials()
    }
}

@Composable
@Preview(widthDp = 480)
fun FirstRunRankMethodPreview() {
    LIFE4Theme {
        FirstRunRankMethod(step = InitialRankSelection(FirstRunPath.NEW_USER_LOCAL))
    }
}

@Composable
@Preview(widthDp = 480, heightDp = 720)
fun FirstRunScreenPreview() {
    val step: FirstRunStep = Username(FirstRunPath.NEW_USER_LOCAL)

    LIFE4Theme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(0.75f)
                    .fillMaxHeight()
            ) {
                FirstRunHeader(
                    showWelcome = step == Landing,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                when (step) {
                    Landing -> { FirstRunNewUser {} }
                    is Username -> { FirstRunUsername(step = step) }
                    is RivalCode -> { FirstRunRivalCode() }
                    is SocialHandles -> { FirstRunSocials() }
                    is InitialRankSelection -> { FirstRunRankMethod(step = step) }
                    is Completed -> {}
                    else -> {}
                }
            }

            if (step.showNextButton) {
                Button(
                    onClick = {},
                    colors = primaryButtonColors(),
                    content = { Text("Next") },
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}
