package com.perrigogames.life4.android.activity.profile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.EXTRA_RANK
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.EXTRA_TARGET_RANK
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_SELECTED
import com.perrigogames.life4.android.activity.profile.RankDetailsActivity.Companion.RESULT_RANK_TARGET_SELECTED
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.ui.ranklist.RankSelectionMini
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.profile.UserRankManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Activity displaying the list of ladder ranks that can be obtained.
 */
class RankListActivity : AppCompatActivity(), KoinComponent {

    private val userRankManager: UserRankManager by inject()

    private val viewRankDetails = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_RANK_SELECTED -> {
                userRankManager.setUserRank(LadderRank.parse(result.data!!.getLongExtra(EXTRA_RANK, 0)))
            }
            RESULT_RANK_TARGET_SELECTED -> {
                userRankManager.setUserTargetRank(LadderRank.parse(result.data!!.getLongExtra(EXTRA_TARGET_RANK, 0)))
            }
            else -> return@registerForActivityResult
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LIFE4Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    var selectedRank by remember {
                        mutableStateOf<LadderRank?>(null)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RankSelectionMini(modifier = Modifier.fillMaxWidth()) {
                            selectedRank = it
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    private fun onRemoveRankClick() {
        userRankManager.setUserRank(null)
        finish()
    }
}
