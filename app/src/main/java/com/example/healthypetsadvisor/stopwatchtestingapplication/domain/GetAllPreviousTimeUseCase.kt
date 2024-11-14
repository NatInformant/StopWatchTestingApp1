package com.example.healthypetsadvisor.stopwatchtestingapplication.domain

import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.data.repositories.MainRepository
import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.data.PreviousTimeElement

class GetAllPreviousTimeUseCase(private val repository: MainRepository) {
    suspend operator fun invoke(): List<PreviousTimeElement> {
        val previousTimeValues = repository.getPreviousTimeFromDb()

        val maxTimeValue = previousTimeValues.maxByOrNull { it.timeIntValue }?.timeIntValue
        val minTimeValue = previousTimeValues.minByOrNull { it.timeIntValue }?.timeIntValue

        return previousTimeValues.map {
            PreviousTimeElement(
                it.timeStringValue,
                when (it.timeIntValue) {
                    maxTimeValue -> R.color.red
                    minTimeValue -> R.color.green
                    else -> R.color.white
                }
            )
        }
    }
}
