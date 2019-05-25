package com.perrigogames.life4trials.ui.trialrecords

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.view.PaddingItemDecoration
import kotlinx.android.synthetic.main.fragment_trial_records.*

class TrialRecordsFragment : Fragment() {

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
            adapter = TrialRecordsAdapter(context!!.life4app.trialManager, listener)
            addItemDecoration(PaddingItemDecoration(resources.getDimensionPixelSize(R.dimen.content_padding_large)))
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))

            text_no_records.visibility = if (adapter!!.itemCount <= 0) VISIBLE else GONE
        }
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
