package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthypetsadvisor.stopwatchtestingapplication.data.database.Time
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.AddNewTimeToDatabaseUseCase
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.ClearTimeTableInDbUseCase
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.GetAllPreviousTimeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val addNewTimeToDatabase: AddNewTimeToDatabaseUseCase,
    private val getAllPreviousTime: GetAllPreviousTimeUseCase,
    private val clearTimeTableInDb: ClearTimeTableInDbUseCase
) : ViewModel() {
    private val _previousTimeList = MutableLiveData<List<String>>()
    val previousTimeList: LiveData<List<String>> = _previousTimeList

    fun updatePreviousTimeList(){
        viewModelScope.launch(Dispatchers.IO) {
            val value = getAllPreviousTime()
            _previousTimeList.postValue(value)
        }
    }

    fun addNewTime(timeValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addNewTimeToDatabase(timeValue)
            updatePreviousTimeList()
        }
    }
    fun clearPreviousTimeFromDb(){
        viewModelScope.launch(Dispatchers.IO) {
            clearTimeTableInDb()
            updatePreviousTimeList()
        }
    }
}
