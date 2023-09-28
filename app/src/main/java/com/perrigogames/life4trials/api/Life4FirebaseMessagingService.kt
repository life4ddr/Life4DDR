package com.perrigogames.life4trials.api

import com.google.firebase.messaging.FirebaseMessagingService

class Life4FirebaseMessagingService: FirebaseMessagingService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
//        sendRegistrationToServer(token)
//        Log.v(TAG, token)
    }

    companion object {
        const val TAG = "Life4Firebase"
    }
}