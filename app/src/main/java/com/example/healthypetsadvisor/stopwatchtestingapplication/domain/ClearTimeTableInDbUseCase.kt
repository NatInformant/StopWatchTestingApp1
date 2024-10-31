package com.example.healthypetsadvisor.stopwatchtestingapplication.domain

import com.example.healthypetsadvisor.stopwatchtestingapplication.data.repositories.MainRepository

class ClearTimeTableInDbUseCase(private val repository: MainRepository) {
    suspend operator fun invoke() =
        repository.clearTimeTableInDb()

}
