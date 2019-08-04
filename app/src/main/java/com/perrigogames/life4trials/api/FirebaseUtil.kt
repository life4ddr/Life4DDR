package com.perrigogames.life4trials.api

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

object FirebaseUtil {

    fun getId(c: Context) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
//                Toast.makeText(c, token, Toast.LENGTH_SHORT).show()
            })
    }
}