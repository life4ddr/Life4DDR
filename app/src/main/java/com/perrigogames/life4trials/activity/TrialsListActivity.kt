package com.perrigogames.life4trials.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.Trial
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.TrialsAdapter
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.util.DisplayMetrics


class TrialsListActivity : AppCompatActivity() {

    private var data: TrialData? = null

    private val useGrid: Boolean get() = intent?.extras?.getBoolean(EXTRA_GRID, true) ?: true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        data = DataUtil.moshi.adapter(TrialData::class.java).fromJson(loadRawString(R.raw.trials))
        if (useGrid) {
            createTiledAdapter()
        } else {
            createListAdapter()
        }
    }

    private fun createListAdapter() {
        recycler_trials_list.adapter =
            TrialsAdapter(this, data!!.trials, false, this::onTrialSelected)
        recycler_trials_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun createTiledAdapter() {
        recycler_trials_list.adapter =
            TrialsAdapter(this, data!!.trials, true, this::onTrialSelected)
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun restartActivity(grid: Boolean) {

        finish()
        startActivity(Intent(this, TrialsListActivity::class.java).apply {
            putExtra(EXTRA_GRID, grid)
        })
    }

    companion object {
        const val EXTRA_GRID = "EXTRA_GRID"
    }
}
