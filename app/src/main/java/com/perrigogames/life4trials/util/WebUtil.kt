package com.perrigogames.life4trials.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.StringRes


fun Context.openWebUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

fun Context.openWebUrlFromRes(@StringRes res: Int) = openWebUrl(getString(res))

fun Context.openWebUrlFromRes(@StringRes res: Int, vararg formatArgs: Any?) = openWebUrl(getString(res, *formatArgs))

val String?.spannedText: Spanned get() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    else
        Html.fromHtml(this)