package com.perrigogames.life4.android.util

import android.content.Context
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.core.widget.doAfterTextChanged
import com.perrigogames.life4.android.R
import com.perrigogames.life4.data.Trial

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(v) { visibility = if (v) View.VISIBLE else View.GONE }

@DrawableRes fun Trial.jacketResId(c: Context): Int =
    c.resources.getIdentifier(id, "drawable", c.packageName).let {
        return if (it == 0) R.drawable.trial_default else it
    }

fun Trial.shouldFetchJacket(c: Context): Boolean =
    coverUrl != null && (jacketResId(c) == R.drawable.trial_default || coverOverride)

inline fun EditText.onFieldChanged(crossinline block: (EditText, Editable) -> Unit) = this.let { field ->
    field.doAfterTextChanged { text ->
        text?.let { block(this, text) }
    }
}

@Composable
fun SizedSpacer(size: Dp) = Spacer(modifier = Modifier.size(size))