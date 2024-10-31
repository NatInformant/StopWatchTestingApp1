package com.example.healthypetsadvisor.stopwatchtestingapplication.domain

import com.example.healthypetsadvisor.stopwatchtestingapplication.data.repositories.MainRepository

class AddNewTimeToDatabaseUseCase(private val repository: MainRepository) {
    suspend operator fun invoke(timeValue: String){
        repository.insertNewTimeToDb(timeValue)
    }
}
