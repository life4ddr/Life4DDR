package com.perrigogames.life4trials.ui.trialrecords

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.ContextMenuRecyclerView.RecyclerViewContextMenuInfo
import kotlinx.android.synthetic.main.fragment_trial_records.*


class TrialRecordsFragment : Fragment() {

    private val trialManager get() = context!!.life4app.trialManager

    private val adapter get() = recycler_records_list.adapter as TrialRecordsAdapter

    private lateinit var viewModel: TrialRecordsViewModel
    private var listener: OnRecordsListInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_trial_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler_records_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = TrialRecordsAdapter(trialManager, listener)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            updateEmptyLabelView()
        }
        registerForContextMenu(recycler_records_list)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrialRecordsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRecordsListInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnRecordsListInteractionListener")
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity!!.menuInflater.inflate(R.menu.menu_record, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as RecyclerViewContextMenuInfo
        //TODO check for Delete
        if (info.position >= 0) {
            trialManager.deleteRecord(info.id)
            adapter.refreshTrials()
            adapter.notifyItemRangeRemoved(info.position, 1)
            updateEmptyLabelView()
        }
        return super.onContextItemSelected(item)
    }

    private fun updateEmptyLabelView() {
        text_no_records.visibilityBool = adapter.itemCount <= 0
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnRecordsListInteractionListener {
        fun onRecordsListInteraction(item: TrialSessionDB)
    }

    companion object {
        fun newInstance() = TrialRecordsFragment()
    }
}
