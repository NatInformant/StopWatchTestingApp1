package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.FragmentMainBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.service.StopwatchState
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.INTENT_ACTION_NAME
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.REQUEST_OVERLAY_PERMISSION
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_COROUTINE_DELAY
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_STATE
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_STOP_TIME
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

    lateinit var bm: LocalBroadcastManager

    override fun onAttach(context: Context) {
        super.onAttach(context);
        bm = LocalBroadcastManager.getInstance(context)
        val actionReceiver = IntentFilter()
        actionReceiver.addAction(INTENT_ACTION_NAME)
        bm.registerReceiver(onStopwatchEventReceived, actionReceiver)
    }


    private val onStopwatchEventReceived = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val stopwatchState = intent.getStringExtra(STOPWATCH_STATE)
                when (stopwatchState) {
                    StopwatchState.RESUME.name -> {
                        startStopwatchTime()
                        setUpUiToStartStopwatch()
                    }
                    StopwatchState.STOP.name -> {
                        stopStopwatchTime()
                        setUpUiToStopStopwatch()
                        stopwatchStopTime = intent.getLongExtra(STOPWATCH_STOP_TIME, 0L)

                        updateStopWatchView(stopwatchStopTime - stopwatchStartTime)
                        Log.d("Recieved stopwatch time", stopwatchCurrentTimeInMilis.toString())
                        viewModel.addNewTime(stopwatchCurrentTime, stopwatchCurrentTimeInMilis)
                    }
                    StopwatchState.RESET.name -> {
                        resetStopwatch()
                        viewModel.clearPreviousTimeFromDb()
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        bm.unregisterReceiver(onStopwatchEventReceived)
    }

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
        if(isStopwatchRunning){
            startStopwatchTime()
        }
    }

    override fun onStop() {
        super.onStop()
        if(!isStopwatchRunning) return

        triggerForegroundService(
            context = requireContext(),
            stopwatchStartTime = stopwatchStartTime
        )
    }

    override fun onResume() {
        triggerForegroundService(
            context = requireContext(),
            stopwatchStartTime = -1L
        )
        super.onResume()
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
                stopwatchStopTime = System.currentTimeMillis()
                val currentStopwatchTime = stopwatchStopTime - stopwatchStartTime
                //Надо бы потестить синхронность работы корутины во фрагменте и корутины в сервисе.
               /* Log.w ("Current stopwatch time in stopwatch job", currentStopwatchTime.toString())*/
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
        stopwatchJob?.cancel()
    }

    private fun setUpUiToStopStopwatch() {
        binding.startOrStopTextview.text = "Resume"
        isStopwatchRunning = false
    }

    private fun updateStopWatchView(timeInMiliSeconds: Long) {
        stopwatchCurrentTimeInMilis = timeInMiliSeconds.toInt()
        stopwatchCurrentTime = getFormattedTime(timeInMiliSeconds)
        /*Log.i("Current stopwatch time in ui", stopwatchCurrentTimeInMilis.toString())*/
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
