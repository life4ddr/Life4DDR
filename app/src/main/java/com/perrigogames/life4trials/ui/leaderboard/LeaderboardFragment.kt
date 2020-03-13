package com.perrigogames.life4trials.ui.leaderboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.manager.PlayerManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * A fragment representing a list of Leaderboard items.
 * Activities containing this fragment MUST implement the
 * [LeaderboardFragment.OnLeaderboardInteractionListener] interface.
 */
class LeaderboardFragment : Fragment(), KoinComponent {

    private val playerManager: PlayerManager by inject()
    private val eventBus: EventBus by inject()

    private var listener: OnLeaderboardInteractionListener? = null
    private lateinit var adapter: LadderLeaderboardAdapter

    private lateinit var leaderboardType: LeaderboardType
    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leaderboardType = LeaderboardType.values()[it.getInt(ARG_LEADERBOARD_TYPE)]
            content = it.getString(ARG_LEADERBOARD_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false)

        adapter = LadderLeaderboardAdapter(listener)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = this@LeaderboardFragment.adapter
            }
        }

        setupLeaderboardContent()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLeaderboardInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLeaderboardInteractionListener")
        }
        eventBus.register(this)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerLadderUpdated(e: PlayerManager.PlayerLadderUpdatedEvent) {
        e.apiPlayers?.let {
            adapter.players = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupLeaderboardContent() {
        when(leaderboardType) {
            LeaderboardType.LADDER -> {
                playerManager.fetchLadderLeaderboards()
            }
            LeaderboardType.TRIAL -> TODO()
        }
    }

    interface OnLeaderboardInteractionListener {
        fun onListFragmentInteraction(item: ApiPlayer?)
    }

    enum class LeaderboardType { LADDER, TRIAL }

    companion object {
        const val ARG_LEADERBOARD_TYPE = "ARG_LEADERBOARD_TYPE"
        const val ARG_LEADERBOARD_CONTENT = "ARG_LEADERBOARD_CONTENT"

        @JvmStatic
        fun newLadderInstance() = newInstance {
            putInt(ARG_LEADERBOARD_TYPE, LeaderboardType.LADDER.ordinal)
        }

        @JvmStatic
        fun newTrialInstance(id: String) = newInstance {
            putInt(ARG_LEADERBOARD_TYPE, LeaderboardType.LADDER.ordinal)
            putString(ARG_LEADERBOARD_CONTENT, id)
        }

        @JvmStatic
        inline fun newInstance(argsFun: Bundle.() -> Unit) =
            LeaderboardFragment().apply {
                arguments = Bundle().also(argsFun)
            }
    }
}
