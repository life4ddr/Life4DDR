package com.perrigogames.life4trials.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.sample.driveapimigration.DriveServiceHelper
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.perrigogames.life4trials.R
import kotlinx.android.synthetic.main.content_google_drive.*

class GoogleDriveActivity: AppCompatActivity() {

    private var mDriveServiceHelper: DriveServiceHelper? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_google_drive)
        button_login_drive.setOnClickListener { requestSignIn() }
        button_logout_drive.setOnClickListener { finish() }
        button_query.setOnClickListener { query() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultData != null) {
                handleSignInResult(resultData)
            }
        }

        super.onActivityResult(requestCode, resultCode, resultData)
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount ->
                Log.d(TAG, "Signed in as " + googleAccount.email!!)

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(this, setOf(DriveScopes.DRIVE_FILE))
                credential.selectedAccount = googleAccount.account
                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build()

                // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                // Its instantiation is required before handling any onClick actions.
                mDriveServiceHelper = DriveServiceHelper(googleDriveService)
            }
            .addOnFailureListener { exception -> Log.e(TAG, "Unable to sign in.", exception) }
    }

    private fun requestSignIn() {
        Log.d(TAG, "Requesting sign-in")

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    /**
     * Saves the currently opened file created via [.createFile] if one exists.
     */
//    private fun saveFile() {
//        if (mDriveServiceHelper != null && mOpenFileId != null) {
//            Log.d(TAG, "Saving $mOpenFileId")
//
//            val fileName = mFileTitleEditText.getText().toString()
//            val fileContent = mDocContentEditText.getText().toString()
//
//            mDriveServiceHelper.saveFile(mOpenFileId, fileName, fileContent)
//                .addOnFailureListener({ exception -> Log.e(TAG, "Unable to save file via REST.", exception) })
//        }
//    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private fun query() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.")

            mDriveServiceHelper!!.queryFiles()
                .addOnSuccessListener { fileList ->
                    val builder = StringBuilder()
                    for (file in fileList.files) {
                        builder.append(file.name).append("\n")
                    }
                    val fileNames = builder.toString()
                    text_drive_status.text = fileNames
                }
                .addOnFailureListener { exception -> Log.e(TAG, "Unable to query files.", exception) }
        }
    }

    companion object {
        private const val TAG = "GoogleDriveActivity"
        private const val REQUEST_CODE_SIGN_IN = 1
    }
}