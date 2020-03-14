package com.perrigogames.life4trials.util

import android.content.Context
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.core.widget.doAfterTextChanged
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4trials.R

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(v) { visibility = if (v) View.VISIBLE else View.GONE }

@DrawableRes fun Trial.jacketResId(c: Context): Int =
    c.resources.getIdentifier(id, "drawable", c.packageName).let {
        return if (it == 0) R.drawable.trial_default else it
    }

inline fun EditText.onFieldChanged(crossinline block: (EditText, Editable) -> Unit) = this.let { field ->
    field.doAfterTextChanged { text ->
        text?.let { block(this, text) }
    }
}
