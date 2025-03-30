package com.perrigogames.life4.android

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.SongResult
import dev.icerock.moko.resources.desc.StringDesc

val InProgressTrialSession.finalPhotoUri: Uri
    get() = Uri.parse(finalPhotoUriString)

val SongResult.photoUri: Uri
    get() = Uri.parse(photoUriString)

@Composable
fun stringResource(res: StringDesc) = res.toString(context = LocalContext.current)
