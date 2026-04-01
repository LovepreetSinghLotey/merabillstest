package com.merabills.question3.ui.main

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.metrics.performance.JankStats
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.merabills.question3.R
import com.merabills.question3.ui.main.adapters.RowAdapter
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private lateinit var jankStatsTracker: JankStats
    private val totalJankyFrames = AtomicInteger(0)

    private val viewModel: MainViewModel by viewModels()
    private lateinit var rowAdapter: RowAdapter
    private val viewPool = RecyclerView.RecycledViewPool().apply {
        setMaxRecycledViews(0, 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleListView(savedInstanceState)
        jankFrameHandler()
    }

    private fun handleListView(savedInstanceState: Bundle?){
        val outerRecyclerView = findViewById<RecyclerView>(R.id.outerRecyclerView)
        outerRecyclerView.layoutManager = LinearLayoutManager(this)
        outerRecyclerView.setHasFixedSize(true)
        outerRecyclerView.itemAnimator = null
        outerRecyclerView.setItemViewCacheSize(20)
        outerRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        rowAdapter = RowAdapter(viewPool) { clickedSquare ->
            viewModel.removeSquare(clickedSquare)
        }
        outerRecyclerView.adapter = rowAdapter

        val restoredRows = if (savedInstanceState != null) viewModel.restoreData(savedInstanceState) else null
        viewModel.initData(restoredRows)

        viewModel.rows.observe(this) { rows ->
            rowAdapter.submitList(rows)
        }
    }

    private fun jankFrameHandler(){
        val totalJankyFramesLiveData = MutableLiveData(0)
        findViewById<Button>(R.id.resetButton).setOnClickListener {
            totalJankyFrames.set(0)
            totalJankyFramesLiveData.value = totalJankyFrames.get()
            viewModel.generateData()
        }

        val jankStatsListener = JankStats.OnFrameListener { frameData ->
            if (frameData.isJank)
                totalJankyFramesLiveData.postValue(totalJankyFrames.incrementAndGet())
        }
        jankStatsTracker = JankStats.createAndTrack(window, jankStatsListener)

        val jankStatsTextView = findViewById<TextView>(R.id.jankStatsTextView)
        totalJankyFramesLiveData.observe(this) { jankyFrames ->
            jankStatsTextView.text = getString(R.string.jank_stats, jankyFrames)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        if (::jankStatsTracker.isInitialized) {
            jankStatsTracker.isTrackingEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (::jankStatsTracker.isInitialized) {
            jankStatsTracker.isTrackingEnabled = false
        }
    }
}
