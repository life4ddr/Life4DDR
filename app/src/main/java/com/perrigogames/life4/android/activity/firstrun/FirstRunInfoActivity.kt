package com.perrigogames.life4.android.activity.firstrun

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.manager.replaceWithInitActivity
import org.koin.core.component.KoinComponent

/**
 * An [AppCompatActivity] shown to the user when their initial stats are empty.
 */
class FirstRunInfoActivity: AppCompatActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LIFE4Theme {
                Surface {
                    FirstRunScreen(
                        onComplete = ::replaceWithInitActivity,
                        onClose = { finish() },
                    )
                }
            }
        }
    }
}
