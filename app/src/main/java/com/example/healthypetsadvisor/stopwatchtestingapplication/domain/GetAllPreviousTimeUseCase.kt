package com.example.healthypetsadvisor.stopwatchtestingapplication.domain

import com.example.healthypetsadvisor.stopwatchtestingapplication.data.repositories.MainRepository

class GetAllPreviousTimeUseCase(private val repository: MainRepository) {
    suspend operator fun invoke() =
        repository.getPreviousTimeFromDb()

}
