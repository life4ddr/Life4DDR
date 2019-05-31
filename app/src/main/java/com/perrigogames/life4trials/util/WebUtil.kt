package com.perrigogames.life4trials.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes

fun Activity.openWebUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

fun Activity.openWebUrlFromRes(@StringRes res: Int) = openWebUrl(getString(res))

fun Activity.openWebUrlFromRes(@StringRes res: Int, vararg formatArgs: Any?) = openWebUrl(getString(res, formatArgs))