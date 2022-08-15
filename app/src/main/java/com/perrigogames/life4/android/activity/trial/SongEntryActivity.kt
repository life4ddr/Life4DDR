package com.perrigogames.life4.android.activity.trial

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4DDRTheme
import com.perrigogames.life4.android.databinding.ContentSongEntryBinding
import com.perrigogames.life4.android.ui.BottomNavigationButtons
import com.perrigogames.life4.android.ui.trial.SongClearButtonType
import com.perrigogames.life4.android.ui.trial.SongClearButtons
import com.perrigogames.life4.android.ui.trial.SongEntryControls
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.model.TrialSessionManager
import com.perrigogames.life4.viewmodel.SongEntryViewModel
import dev.icerock.moko.mvvm.createViewModelFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SongEntryActivity: AppCompatActivity(), KoinComponent {

    private lateinit var binding: ContentSongEntryBinding

    private val trialSessionManager: TrialSessionManager by inject()
    private val currentSession get() = trialSessionManager.currentSession!!

    val result: SongResult? get() = currentSession.results[songIndex]
    val song: Song get() = currentSession.trial.songs[songIndex]

    private val songIndex: Int get() = intent?.extras?.getInt(ARG_SONG_INDEX) ?: 0

    private val requiresAdvancedDetail: Boolean get() =
        intent?.extras?.getSerializable(ARG_ADVANCED_DETAIL) as? Boolean ?: false

    val newEntry: Boolean get() = result?.score == null
    var modified: Boolean = false

//    private val textWatcher = object: TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//            modified = true
//        }
//    }
//    private val perfectTextWatcher = object: TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//            if (clearType.stableId >= PERFECT_FULL_COMBO.stableId) {
//                modified = true
//                val intVal = s?.toString()?.toIntOrNull() ?: 0
//                binding.fieldScore.setText((GameConstants.MAX_SCORE - (intVal * GameConstants.SCORE_PENALTY_PERFECT)).toString())
//                binding.fieldEx.setText((song.ex - intVal).toString())
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LIFE4DDRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SongEntryScreen(
                        songIndex = songIndex,
                        showAdvanced = requiresAdvancedDetail,
                        requireAllData = true,
                    )
                }
            }
        }

//            binding.buttonFc.isEnabled = requiresAdvancedDetail
//            binding.fieldScore.addTextChangedListener(textWatcher)
//            binding.fieldEx.addTextChangedListener(textWatcher)

//            binding.fieldPerfects.addTextChangedListener(perfectTextWatcher)

//            if (binding.fieldScore.requestFocus()) {
//                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
//                    .showSoftInput(binding.fieldScore, InputMethodManager.SHOW_IMPLICIT)
//            }
    }

    override fun onBackPressed() {
        val dialogMessageId = when {
            newEntry -> R.string.photo_save_confirmation
            modified -> R.string.details_save_confirmation
            else -> {
                cancel()
                return
            }
        }
        AlertDialog.Builder(this).setTitle(R.string.are_you_sure)
            .setMessage(dialogMessageId)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.okay) { _, _ -> cancel() }
            .show()
    }

    @Composable
    private fun SongEntryScreen(
        songIndex: Int,
        showAdvanced: Boolean,
        requireAllData: Boolean,
        viewModel: SongEntryViewModel = viewModel(
            factory = createViewModelFactory {
                SongEntryViewModel(
                    songIndex = songIndex,
                    entryState =
                        if (showAdvanced) SongEntryViewModel.EntryState.FULL
                        else SongEntryViewModel.EntryState.BASIC,
                    requireAllData = requireAllData,
                )
            }
        ),
    ) {
        val clearControlsVisible by viewModel.clearControlsVisible.collectAsState()

        Column {
            if (clearControlsVisible) {
                SongClearButtons(onClick = {
                    viewModel.entryState = when(it) {
                        SongClearButtonType.CLEAR -> SongEntryViewModel.EntryState.FULL
                        SongClearButtonType.FC -> SongEntryViewModel.EntryState.FULL_FC
                        SongClearButtonType.PFC -> SongEntryViewModel.EntryState.FULL_PFC
                        SongClearButtonType.MFC -> SongEntryViewModel.EntryState.FULL_MFC
                    }
                })
            }
            AsyncImage(
                model = viewModel.imageUri,
                contentDescription = null,
            )
            SongEntryControls(viewModel)
            BottomNavigationButtons(
                leftText = stringResource(id = R.string.retake_photo),
                rightText = stringResource(id = R.string.use_photo),
                onLeftButtonClicked = { retakePhoto() },
                onRightButtonClicked = {
                    if (viewModel.submit()) {
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        Toast.makeText(this@SongEntryActivity, R.string.make_sure_fields_filled, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun retakePhoto() {
        setResult(RESULT_RETAKE)
        finish()
    }

    private fun cancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        const val RESULT_RETAKE = 101

        const val ARG_SONG_INDEX = "ARG_SONG"
        const val ARG_ADVANCED_DETAIL = "ARG_ADVANCED_DETAIL"
    }
}
