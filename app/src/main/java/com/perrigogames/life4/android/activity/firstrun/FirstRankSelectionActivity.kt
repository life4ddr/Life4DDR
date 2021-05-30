package com.perrigogames.life4.android.activity.firstrun

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_SELECTED
import com.perrigogames.life4.model.FirstRunManager
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity
import com.perrigogames.life4.android.databinding.ActivityFirstRankListBinding
import com.perrigogames.life4.android.manager.finishProcessIntent
import com.perrigogames.life4.android.manager.placementIntent
import com.perrigogames.life4.android.ui.ranklist.RankListFragment
import com.perrigogames.life4.android.ui.ranklist.RankListFragment.OnRankListInteractionListener
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Activity displaying the list of ladder ranks that can be obtained.
 */
class FirstRankSelectionActivity : AppCompatActivity(), OnRankListInteractionListener, KoinComponent {

    private val firstRunManager: FirstRunManager by inject()
    private val ladderManager: LadderManager by inject()

    private lateinit var binding: ActivityFirstRankListBinding

    private val getRankDetails = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_RANK_SELECTED && result.data != null) {
            ladderManager.setUserRank(
                LadderRank.parse(result.data!!.getLongExtra(RankDetailsActivity.EXTRA_RANK, 0))
            )
            startActivity(firstRunManager.finishProcessIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstRankListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPlacements.setOnClickListener { onPlacementsClick() }
        binding.buttonNoRank.setOnClickListener { onNoRankClick() }

        binding.layoutContainer.removeAllViews()
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_container, RankListFragment.newInstance())
            .commit()
    }

    override fun onListFragmentInteraction(item: RankEntry?) =
        getRankDetails.launch(RankDetailsActivity.intent(this, item?.rank, false))

    fun onPlacementsClick() {
        startActivity(firstRunManager.placementIntent(this))
        finish()
    }

    fun onNoRankClick() {
        startActivity(firstRunManager.finishProcessIntent(this))
        finish()
    }
}
