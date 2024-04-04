package com.perrigogames.life4.android

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.SongResult
import dev.icerock.moko.resources.desc.StringDesc

var InProgressTrialSession.finalPhotoUri: Uri
    get() = Uri.parse(finalPhotoUriString)
    set(value) { finalPhotoUriString = value.toString() }

var SongResult.photoUri: Uri
    get() = Uri.parse(photoUriString)
    set(value) { photoUriString = value.toString() }

@Composable
fun stringResource(res: StringDesc) = res.toString(context = LocalContext.current)
