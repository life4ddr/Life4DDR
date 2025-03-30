package com.perrigogames.life4.android.feature.trial

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.util.getSensorRotation
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraBottomSheet(
    bottomSheetState: SheetState,
    onPhotoTaken: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(photoUri) {
        if (photoUri != null) {
            onPhotoTaken(photoUri!!)
        }
    }

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (permissionState.status.isGranted) {
                CameraPreview(
                    onPhotoCaptured = { uri ->
                        photoUri = uri
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }
                )
            } else {
                PermissionReminder()
            }
        }
    }
}

@Composable
fun PermissionReminder(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(
            text = MR.strings.camera_permission_reminder_title.getString(context),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        SizedSpacer(16.dp)
        Text(
            text = MR.strings.camera_permission_reminder_body.getString(context),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun CameraPreview(
    onPhotoCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val outputDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LIFE4")
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    Box(modifier = Modifier.fillMaxSize()) {
        val imageCapture = remember { ImageCapture.Builder().build() }

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                imageCapture.targetRotation = getSensorRotation(
                    x = event.values[0],
                    y = event.values[1]
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                sensorManager.unregisterListener(sensorEventListener)
            }
        })

        AndroidView(
            factory = { _: Context ->
                PreviewView(context).apply {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener(
                        {
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder()
                                .build()
                                .also { it.surfaceProvider = surfaceProvider }

                            // Setup CameraX
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        },
                        ContextCompat.getMainExecutor(context)
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    
        Button(
            onClick = {
                imageCapture?.let { capture ->
                    // Ensure the output directory exists
                    if (!outputDirectory.exists()) {
                        outputDirectory.mkdirs()
                    }
    
                    val photoFile = File(
                        outputDirectory, "photo_${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
                    capture.takePicture(
                        outputOptions,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                correctImageOrientation(photoFile)
                                onPhotoCaptured(Uri.fromFile(photoFile))
                            }
    
                            override fun onError(exception: ImageCaptureException) {
                                exception.printStackTrace()
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "Take Photo")
        }
    }
}

fun correctImageOrientation(file: File) {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    val exif = ExifInterface(file.absolutePath)

    val rotationAngle = when (exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f // No rotation needed
    }

    val uprightBitmap = if (rotationAngle != 0f) {
        val matrix = Matrix()
        matrix.postRotate(rotationAngle)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }

    FileOutputStream(file).use { fos ->
        uprightBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    }
}
