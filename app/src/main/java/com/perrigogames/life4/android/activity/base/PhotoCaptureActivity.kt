package com.perrigogames.life4.android.activity.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    savePhotoToMediaStore(currentUri!!)
                } else {
                    DataUtil.resizeImage(
                        currentPhotoFile!!,
                        PHOTO_SIZE,
                        PHOTO_SIZE,
                        MediaStore.Images.Media.getBitmap(
                            contentResolver, currentUri!!
                        )
                    )
                }
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
                val takeFlags = result.data!!.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                contentResolver.takePersistableUriPermission(currentUri!!, takeFlags)
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
                    DataUtil.createTempFile(this, locale)
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
        Intent(Intent.ACTION_OPEN_DOCUMENT).also { i ->
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/jpeg"
            getPhotoSelection.launch(i)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePhotoToMediaStore(oldPhotoUri: Uri, filename: String = DataUtil.tempFilenameFromLocale(locale)) {
        val resolver = applicationContext.contentResolver
        val photoCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val newPhotos = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.jpg")
            put(MediaStore.Images.Media.IS_PENDING, 1)
            put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "Pictures/LIFE4")
        }

        val newPhotoUri = resolver.insert(photoCollection, newPhotos)

        if (newPhotoUri == null) {
            onPhotoSaveError()
        } else {
            resolver.openFileDescriptor(newPhotoUri, "w", null).use { pfd ->
                if (pfd == null) {
                    onPhotoSaveError()
                } else {
                    DataUtil.resizeImage(
                        pfd.fileDescriptor,
                        PHOTO_SIZE,
                        PHOTO_SIZE,
                        MediaStore.Images.Media.getBitmap(
                            contentResolver, oldPhotoUri
                        )
                    )
                }
            }

            newPhotos.clear()
            newPhotos.put(MediaStore.Audio.Media.IS_PENDING, 0)
            resolver.update(newPhotoUri, newPhotos, null, null)
        }
    }

    abstract fun onPhotoTaken(uri: Uri)

    abstract fun onPhotoChosen(uri: Uri)

    abstract fun onNewPhotoCreated(uri: Uri)

    abstract fun onPhotoCancelled()

    private fun onPhotoSaveError() =
        Toast.makeText(
            this,
            R.string.photo_save_error,
            Toast.LENGTH_SHORT
        ).show()

    private inline fun snackbarListener(
        @StringRes stringRes: Int,
        crossinline listener: (MultiplePermissionsReport?) -> Unit
    ): CompositeMultiplePermissionsListener {
        return CompositeMultiplePermissionsListener(
            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                .with(snackbarContainer, stringRes)
                .withOpenSettingsButton("Settings")
                .build(),
            object: BaseMultiplePermissionsListener() {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) { listener(report) }
            }
        )
    }

    companion object {
        const val PHOTO_SIZE = 1440
    }
}
