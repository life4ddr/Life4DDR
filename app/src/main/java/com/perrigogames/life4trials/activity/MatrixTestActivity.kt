package com.perrigogames.life4trials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRank
import com.perrigogames.life4trials.life4app
import com.perrigogames.life4trials.view.RankHeaderView
import kotlinx.android.synthetic.main.activity_matrix_test.*

class MatrixTestActivity : AppCompatActivity() {

    private val rank: LadderRank? get() = LadderRank.parse(intent?.extras?.getString(EXTRA_RANK))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matrix_test)
        rank?.let {
            layout_progress_matrix.goals = life4app.ladderManager.findRank(it)!!.goals
            (layout_rank_header as RankHeaderView).rank = it
        }
    }

    companion object {
        const val EXTRA_RANK = "EXTRA_RANK"
    }
}
