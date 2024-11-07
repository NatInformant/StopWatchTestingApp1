package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.FragmentMainBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.ACTION_SERVICE_START
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.REQUEST_OVERLAY_PERMISSION
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_COROUTINE_DELAY
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.StopwatchUtils.getFormattedTime
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.StopwatchUtils.triggerForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewBinding<FragmentMainBinding>()
    private var isStopwatchRunning = false
    private val stopwatchDefaultValue = "000:00"
    private val stopwatchListSize = 5
    private var stopwatchStartTime = 0L
    private var stopwatchStopTime = 0L
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
        requestOverlayPermission()
    }

    override fun onStop() {
        super.onStop()

        triggerForegroundService(
            context = requireContext(),
            stopwatchStartTime = stopwatchStartTime
        )
    }

    override fun onResume() {
        super.onResume()
        //hmmm
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
        stopwatchStartTime = 0
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
        stopwatchStartTime =
            if (stopwatchStartTime == 0L)
                System.currentTimeMillis()
            else
                stopwatchStartTime + System.currentTimeMillis() - stopwatchStopTime

        stopwatchJob = lifecycleScope.launch(Dispatchers.Default) {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val currentStopwatchTime = currentTime - stopwatchStartTime
                updateStopWatchView(currentStopwatchTime)
                delay(STOPWATCH_COROUTINE_DELAY)
            }
        }
    }

    private fun setUpUiToStartStopwatch() {
        binding.startOrStopTextview.text = "Stop"
        isStopwatchRunning = true
    }

    private fun stopStopwatchTime() {
        stopwatchStopTime = System.currentTimeMillis()
        stopwatchJob?.cancel()
    }

    private fun setUpUiToStopStopwatch() {
        binding.startOrStopTextview.text = "Resume"
        isStopwatchRunning = false
    }

    private fun updateStopWatchView(timeInMiliSeconds: Long) {
        stopwatchCurrentTimeInMilis = timeInMiliSeconds.toInt()
        stopwatchCurrentTime = getFormattedTime(timeInMiliSeconds)
        stopwatchListAdapter.submitList(List(stopwatchListSize) { stopwatchCurrentTime })
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireActivity().packageName}")
            )
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }
}
