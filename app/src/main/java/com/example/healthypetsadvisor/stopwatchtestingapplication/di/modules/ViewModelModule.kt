package com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules

import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    val viewModelModule = module {
        viewModel<MainViewModel> {
            MainViewModel(
                getAllPreviousTime = get(),
                addNewTimeToDatabase = get(),
                clearTimeTableInDb = get()
            )
        }
    }
}
