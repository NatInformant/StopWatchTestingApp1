package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.FragmentMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewBinding<FragmentMainBinding>()
    private val stopwatchCoroutineDelay = 30L
    private var timeInMiliSeconds = 0L
    private var isStopwatchRunning = false
    private val stopwatchDefaultValue = "000:00"
    private val stopwatchListSize = 5;
    private var previousTime = 0L
    private var stopwatchCurrentTime: String = ""
    private var stopwatchCurrentTimeInMilis: Int = 0
    private var stopwatchJob: Job? = null

    private val stopwatchListAdapter = StopwatchListAdapter()
    private val previousStopwatchListAdapter = PreviousTimeListAdapter()
    private val viewModel by viewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStopwatchList()
        setUpButtonsClickListeners()

        setUpLists()

        viewModel.previousTimeList.observe(viewLifecycleOwner) {
            previousStopwatchListAdapter.submitList(it)
        }
        viewModel.updatePreviousTimeList()
    }

    private fun setUpLists() {
        with(binding.stopwatchList) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = stopwatchListAdapter
            itemAnimator = null
        }

        with(binding.previousTimeList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = previousStopwatchListAdapter
        }
    }

    private fun setUpButtonsClickListeners() {
        binding.resetButton.setOnClickListener {
            stopStopwatchTime()
            resetStopwatch()
            viewModel.clearPreviousTimeFromDb()
        }
        binding.startOrStopTextview.setOnClickListener {
            startOrStopButtonClicked()
        }
        binding.goToTestFragment.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_testFragment)
        }
    }

    private fun initStopwatchList() {
        stopwatchListAdapter.submitList(
            List(stopwatchListSize) { stopwatchDefaultValue }
        )
    }

    private fun resetStopwatch() {
        timeInMiliSeconds = 0
        isStopwatchRunning = false
        binding.startOrStopTextview.text = "Start"
        initStopwatchList()
    }

    fun startOrStopButtonClicked() {
        if (!isStopwatchRunning) {
            startStopwatchTime()
            setUpUiToStartStopwatch()
        } else {
            stopStopwatchTime()
            setUpUiToStopStopwatch()
            viewModel.addNewTime(stopwatchCurrentTime, stopwatchCurrentTimeInMilis)
        }
    }

    private fun startStopwatchTime() {
        previousTime = System.currentTimeMillis()
        stopwatchJob = lifecycleScope.launch(Dispatchers.Default) {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                timeInMiliSeconds += currentTime - previousTime
                previousTime = currentTime
                updateStopWatchView(timeInMiliSeconds)
                delay(stopwatchCoroutineDelay)
            }
        }
    }

    private fun setUpUiToStartStopwatch() {
        binding.startOrStopTextview.text = "Stop"
        isStopwatchRunning = true
    }

    private fun stopStopwatchTime() {
        stopwatchJob?.cancel()
    }

    private fun setUpUiToStopStopwatch() {
        binding.startOrStopTextview.text = "Resume"
        isStopwatchRunning = false
    }

    private fun updateStopWatchView(timeInMiliSeconds: Long) {
        stopwatchCurrentTimeInMilis = timeInMiliSeconds.toInt()
        stopwatchCurrentTime = getFormattedStopWatch((timeInMiliSeconds))
        stopwatchListAdapter.submitList(List(stopwatchListSize) { stopwatchCurrentTime })
    }

    private fun getFormattedStopWatch(ms: Long): String {
        val milliseconds = ms / 10 % 100
        val seconds = ms / 1000

        return "${if (seconds < 10) "00" else if (seconds < 100) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }
}
