package com.perrigogames.life4trials.ui.leaderboard


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.api.ApiPlayer
import com.perrigogames.life4trials.ui.leaderboard.LeaderboardFragment.OnLeaderboardInteractionListener
import com.perrigogames.life4trials.util.nameRes
import com.perrigogames.life4trials.view.RankImageView
import kotlinx.android.synthetic.main.item_ladder_leaderboard.view.*

/**
 * [RecyclerView.Adapter] that can display a [ApiPlayer] and makes a call to the
 * specified [OnLeaderboardInteractionListener].
 */
class LadderLeaderboardAdapter(private val listener: OnLeaderboardInteractionListener?) :
    RecyclerView.Adapter<LadderLeaderboardAdapter.ViewHolder>() {

    var players: List<ApiPlayer> = emptyList()
        set(v) {
            field = v
            notifyDataSetChanged()
        }

    private val mOnClickListener = View.OnClickListener { v ->
        listener?.onListFragmentInteraction(v.tag as ApiPlayer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ladder_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = players[position]
        holder.setPlayer(item)
        holder.setIndex(position)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = players.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val position: TextView = mView.text_position
        val name: TextView = mView.text_name
        val rank: TextView = mView.text_rank
        val rankIcon: RankImageView = mView.image_rank

        fun setIndex(i: Int) {
            position.text = (i + 1).toString()
        }
        fun setPlayer(p: ApiPlayer) {
            name.text = p.name
            rank.text = p.rank?.let { mView.context.getString(it.nameRes) }
            rankIcon.rank = p.rank
        }

        override fun toString() = "${super.toString()} '${position.text} - ${name.text}'"
    }
}
