package com.perrigogames.life4.android.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.DialogMotdBinding
import com.perrigogames.life4.android.util.circularProgressDrawable
import com.perrigogames.life4.model.MotdManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MotdDialog: DialogFragment(), KoinComponent {

    private val motdManager: MotdManager by inject()
    private val settings: Settings by inject()

    private lateinit var binding: DialogMotdBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val motd = motdManager.currentMotd

        binding = DialogMotdBinding.inflate(requireActivity().layoutInflater, null, false)

        binding.textBody.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = HtmlCompat.fromHtml(motd.body, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        binding.buttonClose.apply {
            val color = when {
                motd.useWhiteCloseIcon -> R.color.white
                else -> R.color.black
            }
            setColorFilter(ContextCompat.getColor(requireActivity(), color))
            setOnClickListener { dismiss() }
        }

        Glide.with(binding.root)
            .load(motd.imageUrl)
            .placeholder(circularProgressDrawable(requireActivity()))
            .into(binding.imageHeader)

        settings[SettingsKeys.KEY_LAST_MOTD] = motd.version

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }

    companion object {
        const val TAG = "MessageOfTheDay"
    }
}