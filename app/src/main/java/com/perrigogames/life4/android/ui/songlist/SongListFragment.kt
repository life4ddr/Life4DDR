package com.perrigogames.life4.android.ui.songlist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import co.touchlab.kermit.Logger
import com.perrigogames.life4.BuildConfig
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.view.PathImageView
import com.perrigogames.life4.android.view.SongView
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.feature.trials.TrialManager
import com.perrigogames.life4.feature.trials.TrialSessionManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.PlacementManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * A [Fragment] designed to display a list of [SongView]s in a vertical arrangement.
 */
class SongListFragment : Fragment(), KoinComponent {

    private val logger: Logger by injectLogger("SongListFragment")
    private val placementManager: PlacementManager by inject()
    private val trialManager: TrialManager by inject()
    private val trialSessionManager: TrialSessionManager by inject()

    private val trial get() = trialManager.findTrial(trialId!!) ?: placementManager.findPlacement(trialId!!)!!
    private val results get() = if (useCurrentSession) trialSessionManager.currentSession!!.results else null

    private var trialId: String? = null
    private var tiled: Boolean = false
    private var useCurrentSession: Boolean = false
    private var useCamera: Boolean = true
    private var listener: Listener? = null

    private lateinit var layout: LinearLayout
    private val songViews = mutableListOf<SongView>()
    private var setResultsImageView: ImageView? = null

    private val verificationPhotoHeight: Int by lazy { (layout.height * 0.8).toInt() }

    var shouldShowAdvancedSongDetails: Boolean = false
        set(v) {
            field = v
            songViews.forEach {
                if (useCurrentSession) {
                    it.shouldShowAdvancedSongDetails = v
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trialId = it.getString(ARG_TRIAL_ID)
            tiled = it.getBoolean(ARG_TILED, tiled)
            useCurrentSession = it.getBoolean(ARG_CURRENT_SESSION_RESULTS, false)
            useCamera = it.getBoolean(ARG_SHOW_CAMERA, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!BuildConfig.DEBUG) {
            logger.v("${javaClass.simpleName}: onCreateView: $trialId")
        }
        layout = inflater.inflate(R.layout.fragment_song_list, container, false) as LinearLayout
        trial.songs.forEachIndexed(this::addSongView)
        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun addSongView(idx: Int, song: Song) {
        val newView = SongView(requireContext()).also {
            it.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            it.tag = idx
            it.song = song
            if (results != null) {
                it.result = results!![idx]
            }
            it.shouldShowCamera = useCamera
            it.setOnClickListener { listener?.onSongSelected(song, idx) }
        }
        songViews.add(idx, newView)
        layout.addView(newView)
    }

    fun addResultsPhotoView(uri: Uri) {
        setResultsImageView?.let { layout.removeView(it) }
        if (useCurrentSession) {
            setResultsImageView = PathImageView(requireContext()).also {
                it.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, verificationPhotoHeight).apply {
                    bottomMargin = resources.getDimensionPixelOffset(R.dimen.content_padding_med)
                }
                it.scaleType = ImageView.ScaleType.FIT_CENTER
                it.uri = uri
                it.setOnClickListener { listener?.onFinalImageSelected() }
            }
            layout.addView(setResultsImageView)
        }
    }

    fun setSongResult(idx: Int, result: SongResult?) {
        songViews[idx].result = result
    }

    interface Listener {
        fun onSongSelected(song: Song, position: Int)
        fun onFinalImageSelected() = Unit
    }

    companion object {
        private const val ARG_TRIAL_ID = "ARG_PLACEMENT_ID"
        private const val ARG_TILED = "ARG_TILED"
        private const val ARG_CURRENT_SESSION_RESULTS = "ARG_CURRENT_SESSION_RESULTS"
        private const val ARG_SHOW_CAMERA = "ARG_SHOW_CAMERA"

        @JvmStatic
        fun newInstance(trialId: String, tiled: Boolean, useCurrentSession: Boolean, useCamera: Boolean = true) =
            SongListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TRIAL_ID, trialId)
                    putBoolean(ARG_TILED, tiled)
                    putBoolean(ARG_CURRENT_SESSION_RESULTS, useCurrentSession)
                    putBoolean(ARG_SHOW_CAMERA, useCamera)
                }
            }
    }
}
