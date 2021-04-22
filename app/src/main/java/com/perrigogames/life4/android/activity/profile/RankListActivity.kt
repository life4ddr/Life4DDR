package com.perrigogames.life4.android.activity.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.EXTRA_RANK
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.EXTRA_TARGET_RANK
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_SELECTED
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_TARGET_SELECTED
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.android.ui.ranklist.RankListFragment
import com.perrigogames.life4.android.ui.ranklist.RankListFragment.OnRankListInteractionListener
import com.perrigogames.life4.android.util.visibilityBool
import kotlinx.android.synthetic.main.activity_rank_list.*
import kotlinx.android.synthetic.main.content_rank_list.*
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Activity displaying the list of ladder ranks that can be obtained.
 */
class RankListActivity : AppCompatActivity(), OnRankListInteractionListener, KoinComponent {

    private val ladderManager: LadderManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_list)
        button_remove_rank.visibilityBool = ladderManager.getUserRank() != null

        layout_container.removeAllViews()
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_container,
                RankListFragment.newInstance(3))
            .commit()
    }

    override fun onListFragmentInteraction(item: RankEntry?) =
        startActivityForResult(RankDetailsActivity.intent(this, item?.rank),
            REQUEST_CODE_DETAIL_SELECTION
        )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL_SELECTION && data != null) {
            when (resultCode) {
                RESULT_RANK_SELECTED -> ladderManager.setUserRank(LadderRank.parse(data.getLongExtra(EXTRA_RANK, 0)))
                RESULT_RANK_TARGET_SELECTED -> ladderManager.setUserTargetRank(LadderRank.parse(data.getLongExtra(EXTRA_TARGET_RANK, 0)))
                else -> return
            }
            finish()
        }
    }

    fun onRemoveRankClick(v: View) {
        ladderManager.setUserRank(null)
        finish()
    }

    companion object {
        const val REQUEST_CODE_DETAIL_SELECTION = 1022
    }
}
