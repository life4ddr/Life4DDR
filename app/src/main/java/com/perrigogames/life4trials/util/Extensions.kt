package com.perrigogames.life4trials.util

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.annotation.DrawableRes
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialSession
import com.perrigogames.life4trials.R

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(v) { visibility = if (v) View.VISIBLE else View.GONE }

@DrawableRes fun Trial.jacketResId(c: Context): Int =
    c.resources.getIdentifier(id, "drawable", c.packageName).let {
        return if (it == 0) R.drawable.trial_default else it
    }

var TrialSession.finalPhotoUri: Uri
    get() = Uri.parse(finalPhotoUriString)
    set(value) { finalPhotoUriString = value.toString() }

var SongResult.photoUri: Uri
    get() = Uri.parse(photoUriString)
    set(value) { photoUriString = value.toString() }
