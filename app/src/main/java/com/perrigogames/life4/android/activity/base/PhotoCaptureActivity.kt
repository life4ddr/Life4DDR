package com.perrigogames.life4.android.activity.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.perrigogames.life4.SettingsKeys.KEY_DETAILS_PHOTO_SELECT
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.util.DataUtil
import com.perrigogames.life4.android.util.locale
import com.russhwolf.settings.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.IOException

abstract class PhotoCaptureActivity: AppCompatActivity(), KoinComponent {

    protected val settings: Settings by inject()

    protected var currentPhotoFile: File? = null
    private var currentUri: Uri? = null

    abstract val snackbarContainer: ViewGroup

    private val getPhotoCapture = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                resizeImage(currentPhotoFile!!, currentUri!!)
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(currentPhotoFile.toString()),
                    null,
                    null
                )
                onPhotoTaken(currentUri!!)
            }
            RESULT_CANCELED -> onPhotoCancelled()
        }
        currentPhotoFile = null
        currentUri = null
    }

    private val getPhotoSelection = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                currentUri = result.data!!.data!!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val takeFlags = result.data!!.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    contentResolver.takePersistableUriPermission(currentUri!!, takeFlags)
                }
                onPhotoChosen(currentUri!!)
            }
            RESULT_CANCELED -> onPhotoCancelled()
        }
        currentPhotoFile = null
        currentUri = null
    }

    protected fun acquirePhoto(selection: Boolean = settings.getBoolean(KEY_DETAILS_PHOTO_SELECT, false)) {
        if (selection) {
            startPhotoSelectActivity()
        } else {
            startCameraActivity()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCameraActivity() = Dexter.withActivity(this)
        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(snackbarListener(R.string.camera_permission_description_popup) {
            sendCameraIntent()
        })
        .check()

    @RequiresPermission(allOf = [Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE])
    private fun sendCameraIntent() {
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
                    getPhotoCapture.launch(intent)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startPhotoSelectActivity() = Dexter.withActivity(this)
        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(snackbarListener(R.string.gallery_permission_description_popup) {
            sendPhotoSelectIntent()
        })
        .check()

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun sendPhotoSelectIntent() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent(Intent.ACTION_GET_CONTENT).also { i ->
                i.type = "image/*"
                getPhotoSelection.launch(Intent.createChooser(i, resources.getString(R.string.add_gallery)))
            }
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT).also { i ->
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/jpeg"
                getPhotoSelection.launch(i)
            }
        }
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
}
