package com.perrigogames.life4.android.activity.settings

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ActivityCreditsBinding

class CreditsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val defaultParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            bottomMargin = resources.getDimensionPixelOffset(R.dimen.content_padding_large)
        }
        resources.getStringArray(R.array.credits).forEach { credit ->
            val view = TextView(this).apply {
                text = credit
                setTextSize(COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.text_large).toFloat())
            }
            binding.containerCredits.addView(view, defaultParams)
        }
    }
}