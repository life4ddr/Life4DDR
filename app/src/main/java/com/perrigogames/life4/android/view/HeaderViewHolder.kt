package com.perrigogames.life4.android.view

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HeaderViewHolder(val view: TextView) : RecyclerView.ViewHolder(view) {
    fun bind(headerText: String) {
        view.text = headerText
    }
}
