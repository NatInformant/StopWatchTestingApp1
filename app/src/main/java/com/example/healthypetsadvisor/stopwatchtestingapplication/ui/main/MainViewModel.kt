package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.AddNewTimeToDatabaseUseCase
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.ClearTimeTableInDbUseCase
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.GetAllPreviousTimeUseCase
import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.data.PreviousTimeElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val addNewTimeToDatabase: AddNewTimeToDatabaseUseCase,
    private val getAllPreviousTime: GetAllPreviousTimeUseCase,
    private val clearTimeTableInDb: ClearTimeTableInDbUseCase
) : ViewModel() {
    private val _previousTimeList = MutableLiveData<List<PreviousTimeElement>>()
    val previousTimeList: LiveData<List<PreviousTimeElement>> = _previousTimeList

    fun updatePreviousTimeList() {
        viewModelScope.launch(Dispatchers.IO) {
            val previousTimeValues = getAllPreviousTime()

            val maxTimeValue = previousTimeValues.maxByOrNull { it.timeIntValue }?.timeIntValue
            val minTimeValue = previousTimeValues.minByOrNull { it.timeIntValue }?.timeIntValue

            _previousTimeList.postValue(previousTimeValues.map {
                PreviousTimeElement(
                    it.timeStringValue,
                    when (it.timeIntValue) {
                        maxTimeValue -> R.color.red
                        minTimeValue -> R.color.green
                        else -> R.color.black
                    }
                )
            })
        }
    }

    fun addNewTime(timeStringValue: String, timeIntValue:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            addNewTimeToDatabase(timeStringValue, timeIntValue)
            updatePreviousTimeList()
        }
    }

    fun clearPreviousTimeFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            clearTimeTableInDb()
            updatePreviousTimeList()
        }
    }
}
