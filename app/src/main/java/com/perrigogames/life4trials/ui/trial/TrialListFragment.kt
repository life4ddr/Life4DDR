package com.perrigogames.life4trials.ui.trial

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.SavedRankUpdatedEvent
import com.perrigogames.life4.SettingsKeys.KEY_LIST_HIGHLIGHT_NEW
import com.perrigogames.life4.TrialListReplacedEvent
import com.perrigogames.life4.TrialListUpdatedEvent
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialType
import com.perrigogames.life4trials.R
import com.perrigogames.life4.model.TrialManager
import com.perrigogames.life4trials.view.PaddingItemDecoration
import com.russhwolf.settings.Settings
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * A [Fragment] for displaying a list of [Trial]s in a [RecyclerView].
 */
class TrialListFragment : Fragment(), KoinComponent {

    private lateinit var adapter: TrialListAdapter

    private val trialManager: TrialManager by inject()
    private val settings: Settings by inject()
    private val eventBus: EventBus by inject()
    private val trials: List<Trial> get() = trialManager.activeTrials

    private val featureNew get() = settings.getBoolean(KEY_LIST_HIGHLIGHT_NEW, true)

    private lateinit var recyclerView: RecyclerView

    private var listener: OnTrialListInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recyclerView = inflater.inflate(R.layout.fragment_trial_list, container, false) as RecyclerView
        createTiledAdapter()
        return recyclerView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTrialListInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement Listener")
        }
        eventBus.register(this)
    }

    override fun onDetach() {
        super.onDetach()
        eventBus.unregister(this)
        listener = null
    }

    private fun createTiledAdapter() {
        adapter = TrialListAdapter(trials, featureNew) { id, type -> onTrialSelected(id, type) }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(PaddingItemDecoration(resources.getDimensionPixelSize(R.dimen.content_padding_med)))

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        recyclerView.layoutManager = GridLayoutManager(context,
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) 4 else 2)
    }

    private fun onTrialSelected(trialId: String, trialType: TrialType) = listener?.onTrialSelected(trialId, trialType)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRankUpdated(e: SavedRankUpdatedEvent) {
        adapter.updateNewTrialsList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onListUpdated(e: TrialListUpdatedEvent) {
        adapter.updateNewTrialsList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onListReplaced(e: TrialListReplacedEvent) {
        adapter.featureNew = featureNew
        adapter.updateNewTrialsList()
    }

    interface OnTrialListInteractionListener {
        fun onTrialSelected(trialId: String, trialType: TrialType)
    }

    companion object {
        @JvmStatic fun newInstance() = TrialListFragment()
    }
}
