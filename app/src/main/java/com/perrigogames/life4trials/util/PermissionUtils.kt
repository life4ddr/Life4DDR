package com.perrigogames.life4trials.util

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.perrigogames.life4trials.R


const val FLAG_PERMISSION_REQUEST_CAMERA = 100
const val FLAG_PERMISSION_REQUEST_SELECT = 101

inline fun Activity.askForPhotoSelectPermissions(@StringRes popupTitle: Int,
                                                 @StringRes popupDescription: Int,
                                                 onSuccess: () -> Unit) =
    askForPermissions(popupTitle, popupDescription, arrayOf(WRITE_EXTERNAL_STORAGE), FLAG_PERMISSION_REQUEST_SELECT, onSuccess)

inline fun Activity.askForPhotoTakePermissions(@StringRes popupTitle: Int,
                                               @StringRes popupDescription: Int,
                                               onSuccess: () -> Unit) =
    askForPermissions(popupTitle, popupDescription, arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE), FLAG_PERMISSION_REQUEST_CAMERA, onSuccess)

inline fun Activity.askForPermissions(@StringRes popupTitle: Int,
                                      @StringRes popupDescription: Int,
                                      permissions: Array<String>,
                                      permissionFlag: Int,
                                      onSuccess: () -> Unit) {
    if (permissions.filterNot { checkSelfPermission(this, it) == PERMISSION_GRANTED }.isNotEmpty()) {
        if (permissions.filterNot { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }.isNotEmpty()) {
            AlertDialog.Builder(this).setTitle(popupTitle)
                .setMessage(popupDescription)
                .setPositiveButton(R.string.okay) { _, _ -> ActivityCompat.requestPermissions(this, permissions, permissionFlag) }
                .show()
        } else {
            ActivityCompat.requestPermissions(this, permissions, permissionFlag)
        }
    } else {
        onSuccess()
    }
}