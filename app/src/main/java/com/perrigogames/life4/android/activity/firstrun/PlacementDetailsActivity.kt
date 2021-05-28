package com.perrigogames.life4.android.activity.firstrun

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.perrigogames.life4.SettingsKeys.KEY_DETAILS_PHOTO_SELECT
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.base.PhotoCaptureActivity
import com.perrigogames.life4.android.databinding.ContentPlacementDetailsBinding
import com.perrigogames.life4.android.ui.songlist.SongListFragment
import com.perrigogames.life4.android.view.RankHeaderView
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.model.PlacementManager
import com.russhwolf.settings.set
import org.koin.core.KoinComponent
import org.koin.core.inject


class PlacementDetailsActivity: PhotoCaptureActivity(), SongListFragment.Listener, KoinComponent {

    private val placementManager: PlacementManager by inject()
    private val placementId: String by lazy { intent.extras!!.getString(ARG_PLACEMENT_ID)!! }
    private val trial: Trial get() = placementManager.findPlacement(placementId)!!

    private lateinit var binding: ContentPlacementDetailsBinding

    override val snackbarContainer: ViewGroup get() = binding.container

    private lateinit var songListFragment: SongListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentPlacementDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutRankHeader.apply {
            rank = trial.placementRank!!.toLadderRank()
            genericTitles = true
            navigationListener = object : RankHeaderView.NavigationListener {
                override fun onPreviousClicked() = navigationButtonClicked(placementManager.previousPlacement(placementId))
                override fun onNextClicked() = navigationButtonClicked(placementManager.nextPlacement(placementId))
            }
        }

        binding.includePhotoSourceSelector.switchAcquireMode.apply {
            isChecked = settings.getBoolean(KEY_DETAILS_PHOTO_SELECT, false)
            setOnCheckedChangeListener { _, isChecked ->
                settings[KEY_DETAILS_PHOTO_SELECT] = isChecked
            }
        }

        binding.buttonFinalize.setOnClickListener { onFinalizeClick() }

        songListFragment = SongListFragment.newInstance(trial.id, tiled = false, useCurrentSession = false, useCamera = false)
        supportFragmentManager.beginTransaction()
            .add(R.id.container_song_list_fragment, songListFragment)
            .commitNow()
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.scrollDetails.scrollTo(0, 0)
        } catch (_: NullPointerException) {}
    }

    fun navigationButtonClicked(placement: Trial?) {
        if (placement != null) {
            startActivity(intent(this, placement.id))
            finish()
        }
    }

    override fun onSongSelected(song: Song, position: Int) = Unit

    override fun onNewPhotoCreated(uri: Uri) = Unit

    override fun onPhotoCancelled() = Unit

    fun onFinalizeClick() = acquirePhoto()

    private fun doSubmit() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_placement_title)
            .setMessage(R.string.trial_submit_dialog_prompt)
            .setNegativeButton(R.string.no) { _, _ -> finishWithResult() }
            .setPositiveButton(R.string.yes) { _, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_new_user_submission_form))))
                finishWithResult()
            }
            .show()
    }

    private fun finishWithResult() {
        setResult(RESULT_FINISHED)
        finish()
    }

    override fun onPhotoTaken(uri: Uri) = doSubmit()

    override fun onPhotoChosen(uri: Uri) = doSubmit()

    companion object {
        const val ARG_PLACEMENT_ID = "ARG_PLACEMENT_ID"
        const val RESULT_FINISHED = 1003

        fun intent(c: Context, trialId: String) =
            Intent(c, PlacementDetailsActivity::class.java).apply {
                putExtra(ARG_PLACEMENT_ID, trialId)
            }
    }
}
