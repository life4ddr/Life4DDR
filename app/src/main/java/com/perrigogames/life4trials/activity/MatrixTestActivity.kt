package com.perrigogames.life4trials.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.view.RankHeaderView
import kotlinx.android.synthetic.main.activity_matrix_test.*

class MatrixTestActivity : AppCompatActivity() {

    private lateinit var rank: LadderRank

    private val rankHeader get() = layout_rank_header as RankHeaderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matrix_test)

        rank = LadderRank.parse(intent?.extras?.getLong(EXTRA_RANK)) ?: return
        setRank(rank)
    }

    private fun setRank(r: LadderRank) {
        rank = r
        layout_progress_matrix.goals = life4app.ladderManager.findRank(r)!!.goals
        rankHeader.rank = r
        rankHeader.navigationListener = object : RankHeaderView.NavigationListener {
            override fun onPreviousClicked() = setRank(LadderRank.values()[rank.ordinal - 1])
            override fun onNextClicked() = setRank(LadderRank.values()[rank.ordinal + 1])
        }
    }

    companion object {
        const val EXTRA_RANK = "EXTRA_RANK"

        fun intent(context: Context, rank: LadderRank) = Intent(context, MatrixTestActivity::class.java).also {
            it.putExtra(EXTRA_RANK, rank.stableId)
        }
    }
}
