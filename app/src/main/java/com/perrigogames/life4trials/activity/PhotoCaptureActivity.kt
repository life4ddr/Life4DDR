package com.perrigogames.life4trials.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity.Companion.KEY_DETAILS_PHOTO_SELECT
import com.perrigogames.life4trials.manager.SettingsManager
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.locale
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.IOException

abstract class PhotoCaptureActivity: AppCompatActivity(), KoinComponent {

    private val settingsManager: SettingsManager by inject()

    protected var currentPhotoFile: File? = null
    private var currentUri: Uri? = null

    abstract val snackbarContainer: ViewGroup

    protected fun acquirePhoto(selection: Boolean = settingsManager.getUserFlag(KEY_DETAILS_PHOTO_SELECT, false)) {
        if (selection) {
            startPhotoSelectActivity(FLAG_IMAGE_SELECT)
        } else {
            startCameraActivity(FLAG_IMAGE_CAPTURE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCameraActivity(intentFlag: Int) = Dexter.withActivity(this)
        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(snackbarListener(R.string.camera_permission_description_popup) { sendCameraIntent(intentFlag) })
        .check()

    @RequiresPermission(allOf = [Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE])
    private fun sendCameraIntent(intentFlag: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                try { // Create the File where the photo should go
                    DataUtil.createTempFile(locale)
                } catch (ex: IOException) {
                    null // Error occurred while creating the File
                }?.also {
                    currentPhotoFile = it
                    currentUri = FileProvider.getUriForFile(this, getString(R.string.file_provider_name), it)
                    onNewPhotoCreated(currentUri!!)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, currentUri)
                    startActivityForResult(intent, intentFlag)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startPhotoSelectActivity(intentFlag: Int) = Dexter.withActivity(this)
        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(snackbarListener(R.string.gallery_permission_description_popup) { sendPhotoSelectIntent(intentFlag) })
        .check()

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun sendPhotoSelectIntent(intentFlag: Int) {
        if (Build.VERSION.SDK_INT < 19) {
            Intent().also { i ->
                i.type = "image/*"
                i.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(i, resources.getString(R.string.add_gallery)), intentFlag)
            }
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT).also { i ->
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/jpeg"
                startActivityForResult(i, intentFlag)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            FLAG_IMAGE_CAPTURE -> when (resultCode) {
                RESULT_OK -> {
                    resizeImage(currentPhotoFile!!, currentUri!!)
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                        mediaScanIntent.data = Uri.fromFile(currentPhotoFile)
                        sendBroadcast(mediaScanIntent)
                    }
                    onPhotoTaken(currentUri!!)
                }
                RESULT_CANCELED -> onPhotoCancelled()
            }
            FLAG_IMAGE_SELECT-> when (resultCode) {
                RESULT_OK -> {
                    currentUri = data!!.data!!
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        val takeFlags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        contentResolver.takePersistableUriPermission(currentUri!!, takeFlags)
                    }
                    onPhotoChosen(currentUri!!)
                }
                RESULT_CANCELED -> onPhotoCancelled()
            }
        }
        currentPhotoFile = null
        currentUri = null
    }

    abstract fun onPhotoTaken(uri: Uri)

    abstract fun onPhotoChosen(uri: Uri)

    abstract fun onNewPhotoCreated(uri: Uri)

    abstract fun onPhotoCancelled()

    protected fun resizeImage(out: File, photoUri: Uri) =
        DataUtil.resizeImage(out, 1440, 1440, MediaStore.Images.Media.getBitmap(contentResolver, photoUri))

    private inline fun snackbarListener(@StringRes stringRes: Int,
                                        crossinline listener: (MultiplePermissionsReport?) -> Unit): CompositeMultiplePermissionsListener {
        return CompositeMultiplePermissionsListener(SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
            .with(snackbarContainer, stringRes)
            .withOpenSettingsButton("Settings")
            .build(),
            object: BaseMultiplePermissionsListener() {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) { listener(report) }
            })
    }

    companion object {
        const val FLAG_IMAGE_CAPTURE = 101 // capturing for a new song
        const val FLAG_IMAGE_SELECT = 102 // selecting a local photo for a new song
    }
}
