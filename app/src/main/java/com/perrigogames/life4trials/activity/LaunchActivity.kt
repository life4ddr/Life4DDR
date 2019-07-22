package com.perrigogames.life4trials.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4trials.life4app

/**
 * The first launched activity, determines the path through the startup flow that should be taken
 * based on the current save state.
 */
class LaunchActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launchActivity: Class<*> = try {
            Class.forName(getScreenClassName())
        } catch (e: ClassNotFoundException) {
            PlayerProfileActivity::class.java
        }

        startActivity(Intent(applicationContext, launchActivity))
        finish()
    }

    /** return Class name of Activity to show  */
    private fun getScreenClassName(): String =
        (if (life4app.requireSignin) FirstRunInfoActivity::class else PlayerProfileActivity::class).java.name
}