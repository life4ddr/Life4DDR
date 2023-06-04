package com.perrigogames.life4.android.ui.trial

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.FragmentTrialRecordsBinding
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.ContextMenuRecyclerView.RecyclerViewContextMenuInfo
import com.perrigogames.life4.db.TrialSession
import org.koin.core.component.KoinComponent


class TrialRecordsFragment : Fragment(), KoinComponent {

    private val adapter get() = (binding.recyclerRecordsList as RecyclerView).adapter as TrialRecordsAdapter

    private var _binding: FragmentTrialRecordsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TrialRecordsViewModel by viewModels()
    private var listener: OnRecordsListInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrialRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (binding.recyclerRecordsList as RecyclerView).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = TrialRecordsAdapter(viewModel, listener)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            updateEmptyLabelView()
        }
        registerForContextMenu(binding.recyclerRecordsList)
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
        requireActivity().menuInflater.inflate(R.menu.menu_record, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as RecyclerViewContextMenuInfo
        //TODO check for Delete
        if (info.position >= 0) {
            viewModel.removeRecord(info.id)
            adapter.notifyItemRangeRemoved(info.position, 1)
            updateEmptyLabelView()
        }
        return super.onContextItemSelected(item)
    }

    private fun updateEmptyLabelView() {
        binding.textNoRecords.visibilityBool = adapter.itemCount <= 0
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnRecordsListInteractionListener {
        fun onRecordsListInteraction(item: TrialSession)
    }

    companion object {
        fun newInstance() = TrialRecordsFragment()
    }
}
