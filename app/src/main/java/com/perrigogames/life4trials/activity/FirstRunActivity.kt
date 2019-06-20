package com.perrigogames.life4trials.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.R
import kotlinx.android.synthetic.main.activity_first_run.*

/**
 * A simple [Activity] intended to be shown when there is no user data and an initial rank needs to be established.
 */
class FirstRunActivity : AppCompatActivity() {

    private var selectedOption: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run)
    }

    fun onOptionSelected(v: View) {
        if (selectedOption == null) {
            return
        }
        selectedOption = v.id
        when(selectedOption) {
            R.id.text_option_new -> animateResponse(R.id.text_response_new)
        R.id.text_option_familiar -> animateResponse(R.id.text_response_familiar)
        R.id.text_option_login -> animateResponse(R.id.text_response_login)
    }
}

fun animateResponse(@IdRes responseId: Int) {
    listOf(text_option_new, text_option_familiar, text_option_login,
            text_response_new, text_response_familiar, text_response_login).forEach {

            if (it.id != responseId) {
                it.visibility = View.GONE
            }
        }
    }
}
