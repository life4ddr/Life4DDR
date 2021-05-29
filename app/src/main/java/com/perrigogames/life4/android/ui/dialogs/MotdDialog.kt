package com.perrigogames.life4.android.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.DialogMotdBinding
import com.perrigogames.life4.android.util.circularProgressDrawable
import com.perrigogames.life4.model.MotdManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class MotdDialog: DialogFragment(), KoinComponent {

    private val motdManager: MotdManager by inject()

    lateinit var contentView: View
    lateinit var dialog: AlertDialog
    private lateinit var binding: DialogMotdBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val motd = motdManager.currentMotd
            ?: return AlertDialog.Builder(requireActivity())
                .setTitle(R.string.error)
                .setMessage(R.string.motd_unavailable)
                .setPositiveButton(R.string.okay) { _, _ -> dismiss() }
                .create()

        binding = DialogMotdBinding.inflate(requireActivity().layoutInflater, null, false)

        binding.textBody.text = motd.body

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

        contentView = binding.root
        return AlertDialog.Builder(requireActivity())
            .setView(contentView)
            .setCancelable(false)
            .create()
    }

    companion object {
        const val TAG = "MessageOfTheDay"
    }
}