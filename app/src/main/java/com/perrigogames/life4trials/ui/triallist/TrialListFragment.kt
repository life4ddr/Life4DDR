package com.perrigogames.life4trials.ui.triallist

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.event.SavedRankUpdatedEvent
import com.perrigogames.life4trials.event.TrialListUpdatedEvent
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.view.PaddingItemDecoration
import org.greenrobot.eventbus.Subscribe

/**
 * A [Fragment] for displaying a list of [Trial]s in a [RecyclerView].
 */
class TrialListFragment : Fragment() {

    private lateinit var adapter: TrialListAdapter

    private val trialManager get() = context!!.life4app.trialManager
    private val trials: List<Trial> get() = trialManager.trials

    private lateinit var recyclerView: RecyclerView

    private var tiled: Boolean = false
    private var listener: OnTrialListInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tiled = it.getBoolean(ARG_TILED, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recyclerView = inflater.inflate(R.layout.fragment_trial_list, container, false) as RecyclerView
        if (tiled) {
            createTiledAdapter()
        } else {
            createListAdapter()
        }
        return recyclerView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTrialListInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement Listener")
        }
        Life4Application.eventBus.register(this)
    }

    override fun onDetach() {
        super.onDetach()
        Life4Application.eventBus.unregister(this)
        listener = null
    }

    private fun createListAdapter() {
        adapter = TrialListAdapter(context!!, trials, false) { listener?.onTrialSelected(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    private fun createTiledAdapter() {
        adapter = TrialListAdapter(context!!, trials, true) { listener?.onTrialSelected(it) }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(PaddingItemDecoration(resources.getDimensionPixelSize(R.dimen.content_padding_med)))

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        recyclerView.layoutManager = GridLayoutManager(context,
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) 4 else 2)
    }

    @Subscribe
    fun onRankUpdated(e: SavedRankUpdatedEvent) {
        if (e.trial != null) {
            adapter.notifyItemChanged(trials.indexOf(e.trial))
        } else {
            adapter.notifyDataSetChanged()
        }
    }

    @Subscribe
    fun onListUpdated(e: TrialListUpdatedEvent) {
        adapter.notifyDataSetChanged()
    }

    interface OnTrialListInteractionListener {

        fun onTrialSelected(trialId: String)
    }

    companion object {

        private const val ARG_TILED = "ARG_TILED"

        @JvmStatic
        fun newInstance(tiled: Boolean) =
            TrialListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_TILED, tiled)
                }
            }
    }
}
