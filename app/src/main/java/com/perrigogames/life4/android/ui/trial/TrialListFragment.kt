package com.perrigogames.life4.android.ui.trial

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.SavedRankUpdatedEvent
import com.perrigogames.life4.TrialListReplacedEvent
import com.perrigogames.life4.TrialListUpdatedEvent
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.view.PaddingItemDecoration
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.TrialType
import com.perrigogames.life4.model.TrialManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * A [Fragment] for displaying a list of [Trial]s in a [RecyclerView].
 */
class TrialListFragment : Fragment(), KoinComponent {

    private lateinit var adapter: TrialListAdapter

    private val trialManager: TrialManager by inject()
    private val eventBus: EventBus by inject()

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
        val state = trialManager.createViewState()
        adapter = TrialListAdapter(state) { id, type -> onTrialSelected(id, type) }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(PaddingItemDecoration(resources.getDimensionPixelSize(R.dimen.content_padding_med)))

        val displayMetrics = resources.displayMetrics
        val spanCount = if (displayMetrics.widthPixels > displayMetrics.heightPixels) 4 else 2
        recyclerView.layoutManager = GridLayoutManager(context, spanCount).apply {
            spanSizeLookup = adapter.spanSizeLookup
        }
    }

    private fun onTrialSelected(trialId: String, trialType: TrialType) = listener?.onTrialSelected(trialId, trialType)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRankUpdated(e: SavedRankUpdatedEvent) {
        adapter.state = trialManager.createViewState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onListUpdated(e: TrialListUpdatedEvent) {
        adapter.state = trialManager.createViewState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onListReplaced(e: TrialListReplacedEvent) {
        adapter.state = trialManager.createViewState()
    }

    interface OnTrialListInteractionListener {
        fun onTrialSelected(trialId: String, trialType: TrialType)
    }

    companion object {
        @JvmStatic fun newInstance() = TrialListFragment()
    }
}
