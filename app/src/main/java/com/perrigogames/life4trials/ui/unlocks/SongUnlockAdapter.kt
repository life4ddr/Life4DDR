package com.perrigogames.life4trials.ui.unlocks

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.IgnoreGroup
import com.perrigogames.life4trials.data.IgnoreUnlockType
import com.perrigogames.life4trials.data.IgnoreUnlockType.*
import com.perrigogames.life4trials.data.IgnoredSong
import com.perrigogames.life4trials.util.CommonSizes
import com.perrigogames.life4trials.util.visibilityBool
import com.perrigogames.life4trials.view.DifficultyTextView
import kotlinx.android.synthetic.main.item_unlocks_base.view.*

class SongUnlockAdapter: RecyclerView.Adapter<SongUnlockAdapter.BaseUnlockViewHolder>() {

    var listener: ((IgnoreGroup, Long) -> Unit)? = null
    var selectionReader: ((String) -> List<Boolean>)? = null

    var ignoreGroups: List<IgnoreGroup> = emptyList()
        set(v) {
            field = v.filter { it.unlock != null }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseUnlockViewHolder {
        val base = LayoutInflater.from(parent.context).inflate(R.layout.item_unlocks_base, parent, false)
        return when(IgnoreUnlockType.values()[viewType]) {
            SINGLE -> SelectionViewHolder(base)
            SEQUENTIAL -> SequenceViewHolder(base)
            ALL -> AllViewHolder(base)
        }
    }

    override fun onBindViewHolder(holder: BaseUnlockViewHolder, position: Int) {
        val item = ignoreGroups[position]
        holder.bind(item.name, item.songs) { selection -> listener?.invoke(item, item.unlock!!.toStoredState(selection)) }
        selectionReader?.invoke(item.id)?.let { holder.setSelection(it) }
    }

    override fun getItemCount() = ignoreGroups.size

    override fun getItemViewType(position: Int) = ignoreGroups[position].unlock!!.ordinal

    abstract inner class BaseUnlockViewHolder(v: View): RecyclerView.ViewHolder(v) {

        protected lateinit var unlockViews: List<CompoundButton>
        protected lateinit var selectionListener: (List<Boolean>) -> Unit

        open val usesCheckbox = false

        open fun bind(title: String, unlockItems: List<IgnoredSong>, listener: (List<Boolean>) -> Unit) {
            selectionListener = listener

            itemView.check_all_unlock.visibilityBool = usesCheckbox
            itemView.text_title.text = title
            itemView.text_title.setOnClickListener {
                itemView.check_all_unlock.apply { isChecked = !isChecked }
            }

            itemView.layout_unlock_container.removeAllViews()
            unlockViews = unlockItems.mapIndexed { idx, unlock ->
                createUnlockItemView(itemView.context, idx).apply {
                    text = unlock.title
                    if (unlock.difficultyClass != null) {
                        itemView.layout_unlock_container.addView(LinearLayout(itemView.context).also { rowContainer ->
                            rowContainer.addView(this)
                            rowContainer.addView(DifficultyTextView(itemView.context).also { difficultyLabel ->
                                difficultyLabel.difficultyClass = unlock.difficultyClass
                                difficultyLabel.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                                    params.marginStart = CommonSizes.contentPaddingSmall(resources)
                                }
                            })
                        })
                    } else {
                        itemView.layout_unlock_container.addView(this)
                    }
                }
            }
        }

        abstract fun createUnlockItemView(context: Context, index: Int): CompoundButton

        open fun setSelection(selection: List<Boolean>) {
            unlockViews.forEachIndexed { idx, button ->
                button.isChecked = selection[idx]
            }
        }
    }

    /**
     * Holder for an unlock that happens all at once, no in-betweens.
     */
    inner class AllViewHolder(v: View): BaseUnlockViewHolder(v) {

        override val usesCheckbox = true

        override fun bind(title: String, unlockItems: List<IgnoredSong>, listener: (List<Boolean>) -> Unit) {
            super.bind(title, unlockItems, listener)
            itemView.check_all_unlock.setOnCheckedChangeListener { _, checked ->
                unlockViews.forEach { it.isChecked = checked }
                selectionListener(unlockViews.map { checked })
            }
        }

        override fun createUnlockItemView(context: Context, index: Int) = CheckBox(context).apply {
            isEnabled = false
        }

        override fun setSelection(selection: List<Boolean>) {
            super.setSelection(selection)
            itemView.check_all_unlock.isChecked = selection[0]
        }
    }

    /**
     * Holder for an unlock that happens in sequence.
     */
    inner class SequenceViewHolder(v: View): BaseUnlockViewHolder(v) {

        override fun createUnlockItemView(context: Context, index: Int) = RadioButton(context).apply {
            setOnClickListener {
                unlockViews.forEachIndexed { idx, button -> button.isChecked = idx.toLong() < index.toLong() + 1 }
                selectionListener(unlockViews.map { it.isChecked })
            }
        }
    }

    /**
     * Holder for an unlock that goes one at a time in any order.
     */
    inner class SelectionViewHolder(v: View): BaseUnlockViewHolder(v) {

        override fun createUnlockItemView(context: Context, index: Int) = CheckBox(context).apply {
            setOnClickListener {
                selectionListener(unlockViews.map { it.isChecked })
            }
        }
    }
}
