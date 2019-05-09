package com.perrigogames.life4trials.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.TrialsAdapter
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.event.SavedRankUpdatedEvent
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.SharedPrefsUtils
import com.perrigogames.life4trials.util.loadRawString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.Subscribe

/**
 * Activity for presenting the list of trials to the user. Tapping on a trial will open
 * an associated [TrialDetailsActivity].
 */
class TrialsListActivity : AppCompatActivity() {

    private lateinit var data: TrialData
    private lateinit var adapter: TrialsAdapter

    private val useGrid: Boolean get() = intent?.extras?.getBoolean(EXTRA_GRID, true) ?: true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        data = DataUtil.moshi.adapter(TrialData::class.java).fromJson(loadRawString(R.raw.trials))!!
        if (BuildConfig.DEBUG) {
            val debugData: TrialData = DataUtil.moshi.adapter(TrialData::class.java).fromJson(loadRawString(R.raw.trials_debug))!!
            data = TrialData(data.trials + debugData.trials)
        }

        if (useGrid) {
            createTiledAdapter()
        } else {
            createListAdapter()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Life4Application.eventBus.register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Life4Application.eventBus.unregister(this)
    }

    @Subscribe
    fun onRankUpdated(event: SavedRankUpdatedEvent) {
        adapter.notifyItemChanged(data.trials.indexOf(event.trial))
    }

    private fun createListAdapter() {
        adapter = TrialsAdapter(this, data.trials, false, this::onTrialSelected)
        recycler_trials_list.adapter = adapter
        recycler_trials_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun createTiledAdapter() {
        adapter = TrialsAdapter(this, data.trials, true, this::onTrialSelected)
        recycler_trials_list.adapter = adapter
        recycler_trials_list.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val padding = resources.getDimensionPixelSize(R.dimen.content_padding_med)
                outRect.set(padding, padding, padding, padding)
            }
        })

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        recycler_trials_list.layoutManager = GridLayoutManager(this,
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) 4 else 2)
    }

    private fun onTrialSelected(trial: Trial) {
        startActivity(Intent(this, TrialDetailsActivity::class.java).apply {
            putExtra(TrialDetailsActivity.ARG_TRIAL, trial)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(if (useGrid) R.menu.menu_main_grid else R.menu.menu_main_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_list_view -> {
                restartActivity(false)
                true
            }
            R.id.action_grid_view -> {
                restartActivity(true)
                true
            }
            R.id.action_clear_rank_data -> {
                clearRankData()
                true
            }
            R.id.action_drive -> {
                startActivity(Intent(this, GoogleDriveActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun restartActivity(grid: Boolean = useGrid) {
        intent.putExtra(EXTRA_GRID, grid)
        recreate()
    }

    private fun clearRankData() {
        AlertDialog.Builder(this)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.confirm_erase_data)
            .setPositiveButton(R.string.yes) { _, _ ->
                SharedPrefsUtils.clearRanks(this)
                restartActivity()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    companion object {
        const val EXTRA_GRID = "EXTRA_GRID"
    }
}
