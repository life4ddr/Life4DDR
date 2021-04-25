package com.perrigogames.life4.android.activity.firstrun

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_SELECTED
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity
import com.perrigogames.life4.android.manager.finishProcessIntent
import com.perrigogames.life4.android.manager.placementIntent
import com.perrigogames.life4.android.ui.ranklist.RankListFragment
import com.perrigogames.life4.android.ui.ranklist.RankListFragment.OnRankListInteractionListener
import kotlinx.android.synthetic.main.content_rank_list.*
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Activity displaying the list of ladder ranks that can be obtained.
 */
class FirstRankSelectionActivity : AppCompatActivity(), OnRankListInteractionListener, KoinComponent {

    private val firstRunManager: FirstRunManager by inject()
    private val ladderManager: LadderManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_rank_list)

        layout_container.removeAllViews()
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_container,
                RankListFragment.newInstance(3))
            .commit()
    }

    override fun onListFragmentInteraction(item: RankEntry?) =
        startActivityForResult(
            RankDetailsActivity.intent(
                this,
                item?.rank,
                false
            ),
            REQUEST_CODE_DETAIL_SELECTION
        )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL_SELECTION) {
            if (resultCode == RESULT_RANK_SELECTED && data != null) {
                ladderManager.setUserRank(LadderRank.parse(data.getLongExtra(RankDetailsActivity.EXTRA_RANK, 0)))
                startActivity(firstRunManager.finishProcessIntent(this))
                finish()
            }
        }
    }

    fun onPlacementsClick(v: View) {
        startActivity(firstRunManager.placementIntent(this))
        finish()
    }

    fun onNoRankClick(v: View) {
        startActivity(firstRunManager.finishProcessIntent(this))
        finish()
    }

    companion object {
        const val REQUEST_CODE_DETAIL_SELECTION = 1022
    }
}
