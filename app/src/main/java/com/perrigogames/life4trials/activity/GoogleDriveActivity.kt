package com.perrigogames.life4trials.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
import com.google.android.gms.tasks.OnFailureListener
import android.provider.MediaStore
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.drive.DriveClient
import com.google.android.gms.drive.DriveResourceClient
import com.google.android.gms.tasks.Task
import android.content.Intent.getIntent
import com.perrigogames.life4trials.Life4Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class GoogleDriveActivity: AppCompatActivity() {

    private lateinit var signInClient: GoogleSignInClient
    private lateinit var driveClient: DriveClient
    private lateinit var driveResourceClient: DriveResourceClient

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInClient = buildGoogleSignInClient()

        val credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE)
        credential.setSelectedAccountName(accountName)
        val service = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential).build()
//
//        try {
//            // Try to perform a Drive API request, for instance:
//            File file = service.files().insert(body, mediaContent).execute();
//        } catch (e: UserRecoverableAuthIOException) {
//            startActivityForResult(e.getIntent(), COMPLETE_AUTHORIZATION_REQUEST_CODE)
//        }

//        GlobalScope.launch(Dispatchers.Main) {
//            val postRequest = Life4Application.driveAPI.getFiles()
//            try {
//                val response = postRequest.await()
//                val posts = response.body()
//            } catch (e: Exception) { }
//        }

    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .build()
        return GoogleSignIn.getClient(this, signInOptions)
    }

    private fun updateViewWithGoogleSignInAccountTask(task:Task<GoogleSignInAccount>) {
        Log.i(TAG, "Update view with sign in account task")
        task.addOnSuccessListener { googleSignInAccount ->
            Log.i(TAG, "Sign in success")
            driveClient = Drive.getDriveClient(applicationContext, googleSignInAccount)
            driveResourceClient = Drive.getDriveResourceClient(applicationContext, googleSignInAccount)
            // Start camera.
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE)
        }.addOnFailureListener { e -> Log.w(TAG, "Sign in failed", e) }
    }

    companion object {
        val TAG = GoogleDriveActivity::class.simpleName
        const val REQUEST_CODE_CAPTURE_IMAGE = 100
    }
}