package com.perrigogames.life4.android.activity.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.EXTRA_RANK
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.EXTRA_TARGET_RANK
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_SELECTED
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_TARGET_SELECTED
import com.perrigogames.life4.android.databinding.ActivityRankListBinding
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.android.ui.ranklist.RankListFragment
import com.perrigogames.life4.android.ui.ranklist.RankListFragment.OnRankListInteractionListener
import com.perrigogames.life4.android.util.visibilityBool
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Activity displaying the list of ladder ranks that can be obtained.
 */
class RankListActivity : AppCompatActivity(), OnRankListInteractionListener, KoinComponent {

    private val ladderManager: LadderManager by inject()

    private lateinit var binding: ActivityRankListBinding

    private val viewRankDetails = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_RANK_SELECTED -> {
                ladderManager.setUserRank(LadderRank.parse(result.data!!.getLongExtra(EXTRA_RANK, 0)))
            }
            RESULT_RANK_TARGET_SELECTED -> {
                ladderManager.setUserTargetRank(LadderRank.parse(result.data!!.getLongExtra(EXTRA_TARGET_RANK, 0)))
            }
            else -> return@registerForActivityResult
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonRemoveRank.visibilityBool = ladderManager.getUserRank() != null
        binding.buttonRemoveRank.setOnClickListener { onRemoveRankClick() }

        binding.layoutContainer.removeAllViews()
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_container, RankListFragment.newInstance())
            .commit()
    }

    override fun onListFragmentInteraction(item: RankEntry?) =
        viewRankDetails.launch(RankDetailsActivity.intent(this, item?.rank))

    private fun onRemoveRankClick() {
        ladderManager.setUserRank(null)
        finish()
    }
}
