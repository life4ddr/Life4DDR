package com.perrigogames.life4.android.ui.unlocks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.data.IgnoreGroup
import com.perrigogames.life4.data.IgnoreUnlockType
import com.perrigogames.life4.data.IgnoreUnlockType.*
import com.perrigogames.life4.data.IgnoredSong
import com.perrigogames.life4.android.databinding.ItemUnlocksBaseBinding
import com.perrigogames.life4.android.util.CommonSizes
import com.perrigogames.life4.android.util.visibilityBool
import com.perrigogames.life4.android.view.DifficultyTextView

class SongUnlockAdapter: RecyclerView.Adapter<SongUnlockAdapter.BaseUnlockViewHolder>() {

    var listener: ((IgnoreGroup, Long) -> Unit)? = null
    var selectionReader: ((String) -> List<Boolean>)? = null

    var ignoreGroups: List<IgnoreGroup> = emptyList()
        set(v) {
            field = v.filter { it.unlock != null }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseUnlockViewHolder {
        val base = ItemUnlocksBaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    abstract inner class BaseUnlockViewHolder(protected val binding: ItemUnlocksBaseBinding): RecyclerView.ViewHolder(binding.root) {

        private val context: Context get() = itemView.context

        protected lateinit var unlockViews: List<CompoundButton>
        protected lateinit var selectionListener: (List<Boolean>) -> Unit

        open val usesCheckbox = false

        open fun bind(title: String, unlockItems: List<IgnoredSong>, listener: (List<Boolean>) -> Unit) {
            selectionListener = listener

            binding.checkAllUnlock.visibilityBool = usesCheckbox
            binding.buttonClear.visibilityBool = !usesCheckbox
            binding.buttonClear.setOnClickListener {
                unlockViews.forEach { it.isChecked = false }
                reportUnlockViewsSelection()
            }
            binding.textTitle.text = title
            binding.textTitle.setOnClickListener {
                binding.checkAllUnlock.apply { isChecked = !isChecked }
            }

            binding.layoutUnlockContainer.removeAllViews()
            unlockViews = unlockItems.mapIndexed { idx, unlock ->
                createUnlockItemView(context, idx).apply {
                    text = unlock.title
                    if (unlock.difficultyClass != null) {
                        binding.layoutUnlockContainer.addView(LinearLayout(context).also { rowContainer ->
                            rowContainer.addView(this)
                            rowContainer.addView(DifficultyTextView(context).apply {
                                difficultyClass = unlock.difficultyClass!!
                                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                                    params.marginStart = CommonSizes.contentPaddingSmall(resources)
                                }
                            })
                        })
                    } else {
                        binding.layoutUnlockContainer.addView(this)
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

        protected fun reportUnlockViewsSelection() {
            selectionListener(unlockViews.map { it.isChecked })
        }
    }

    /**
     * Holder for an unlock that happens all at once, no in-betweens.
     */
    inner class AllViewHolder(binding: ItemUnlocksBaseBinding): BaseUnlockViewHolder(binding) {

        override val usesCheckbox = true

        override fun bind(title: String, unlockItems: List<IgnoredSong>, listener: (List<Boolean>) -> Unit) {
            super.bind(title, unlockItems, listener)
            binding.checkAllUnlock.setOnCheckedChangeListener { _, checked ->
                unlockViews.forEach { it.isChecked = checked }
                reportUnlockViewsSelection()
            }
        }

        override fun createUnlockItemView(context: Context, index: Int) = CheckBox(context).apply {
            isEnabled = false
        }

        override fun setSelection(selection: List<Boolean>) {
            super.setSelection(selection)
            binding.checkAllUnlock.isChecked = selection[0]
        }
    }

    /**
     * Holder for an unlock that happens in sequence.
     */
    inner class SequenceViewHolder(binding: ItemUnlocksBaseBinding): BaseUnlockViewHolder(binding) {

        override fun createUnlockItemView(context: Context, index: Int) = RadioButton(context).apply {
            setOnClickListener {
                unlockViews.forEachIndexed { idx, button -> button.isChecked = idx.toLong() < index.toLong() + 1 }
                reportUnlockViewsSelection()
            }
        }
    }

    /**
     * Holder for an unlock that goes one at a time in any order.
     */
    inner class SelectionViewHolder(binding: ItemUnlocksBaseBinding): BaseUnlockViewHolder(binding) {

        override fun createUnlockItemView(context: Context, index: Int) = CheckBox(context).apply {
            setOnClickListener {
                reportUnlockViewsSelection()
            }
        }
    }
}
