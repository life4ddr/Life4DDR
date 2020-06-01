package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4trials.R
import kotlinx.android.synthetic.main.view_ex_score_view.view.*

class RunningEXScoreView @JvmOverloads constructor(context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    fun update(session: InProgressTrialSession) {
        progress_ex_score.apply {
            progress = session.currentTotalExScore
            secondaryProgress = session.currentMaxExScore
            max = session.trial.total_ex
        }
        text_ex_current.text = context.getString(
            R.string.ex_score_missing_string_format, session.currentTotalExScore, session.missingExScore * -1)
        text_ex_goal.text = if (session.currentMaxExScore == session.trial.total_ex)
            context.getString(R.string.ex_score_string_format, session.currentMaxExScore)
        else
            context.getString(R.string.ex_score_progress_format, session.currentMaxExScore, session.trial.total_ex)
    }
}
