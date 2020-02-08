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
    var selectionReader: ((String) -> Long)? = null

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
        holder.bind(item.name, item.songs) { selection -> listener?.invoke(item, selection) }
        selectionReader?.invoke(item.id)?.let { holder.setSelectionCount(it) }
    }

    override fun getItemCount() = ignoreGroups.size

    override fun getItemViewType(position: Int) = ignoreGroups[position].unlock!!.ordinal

    abstract inner class BaseUnlockViewHolder(v: View): RecyclerView.ViewHolder(v) {

        protected lateinit var unlockViews: List<CompoundButton>
        protected lateinit var selectionListener: (Long) -> Unit

        open val usesCheckbox = false

        open fun bind(title: String, unlockItems: List<IgnoredSong>, listener: (Long) -> Unit) {
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

        abstract fun setSelectionCount(selection: Long)
    }

    /**
     * Holder for an unlock that happens all at once, no in-betweens.
     */
    inner class AllViewHolder(v: View): BaseUnlockViewHolder(v) {

        override val usesCheckbox = true

        override fun bind(title: String, unlockItems: List<IgnoredSong>, listener: (Long) -> Unit) {
            super.bind(title, unlockItems, listener)
            itemView.check_all_unlock.setOnCheckedChangeListener { _, checked ->
                unlockViews.forEach { it.isChecked = checked }
                selectionListener(if (checked) 1L else 0L)
            }
        }

        override fun createUnlockItemView(context: Context, index: Int) = CheckBox(context).apply {
            isEnabled = false
        }

        override fun setSelectionCount(selection: Long) {
            val check = selection == 1L
            itemView.check_all_unlock.isChecked = check
        }
    }

    /**
     * Holder for an unlock that happens in sequence.
     */
    inner class SequenceViewHolder(v: View): BaseUnlockViewHolder(v) {

        override fun createUnlockItemView(context: Context, index: Int) = RadioButton(context).apply {
            setOnClickListener {
                setSelectionCount(index.toLong() + 1)
                selectionListener(index.toLong() + 1)
            }
        }

        override fun setSelectionCount(selection: Long) {
            unlockViews.forEachIndexed { idx, button ->
                button.isChecked = idx.toLong() < selection
            }
        }
    }

    /**
     * Holder for an unlock that goes one at a time in any order.
     */
    inner class SelectionViewHolder(v: View): BaseUnlockViewHolder(v) {

        private var selection = 0L

        override fun createUnlockItemView(context: Context, index: Int) = CheckBox(context).apply {
            setOnClickListener {
                val bitIdx = 1L.shl(index)
                selection = selection xor bitIdx
                selectionListener(selection)
            }
        }

        override fun setSelectionCount(selection: Long) {
            this.selection = selection
            unlockViews.forEachIndexed { idx, button ->
                val bitIdx = 1L.shl(idx)
                button.isChecked = selection and bitIdx != 0L
            }
        }
    }
}