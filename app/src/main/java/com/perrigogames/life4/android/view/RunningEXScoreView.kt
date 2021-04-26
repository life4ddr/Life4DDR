package com.perrigogames.life4.android.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ViewExScoreViewBinding

class RunningEXScoreView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewExScoreViewBinding.bind(this)

    fun update(session: InProgressTrialSession) {
        binding.progressExScore.apply {
            progress = session.currentTotalExScore
            secondaryProgress = session.currentMaxExScore
            max = session.trial.totalEx
        }
        binding.textExCurrent.text = context.getString(
            R.string.ex_score_missing_string_format, session.currentTotalExScore, session.missingExScore * -1)
        binding.textExGoal.text = if (session.currentMaxExScore == session.trial.totalEx)
            context.getString(R.string.ex_score_string_format, session.currentMaxExScore)
        else
            context.getString(R.string.ex_score_progress_format, session.currentMaxExScore, session.trial.totalEx)
    }
}
