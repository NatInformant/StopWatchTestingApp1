package com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules

import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.AddNewTimeToDatabaseUseCase
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.ClearTimeTableInDbUseCase
import com.example.healthypetsadvisor.stopwatchtestingapplication.domain.GetAllPreviousTimeUseCase
import org.koin.dsl.module

object DomainModule {
    val domainModule = module {
        single<AddNewTimeToDatabaseUseCase> { AddNewTimeToDatabaseUseCase(repository = get()) }
        single<GetAllPreviousTimeUseCase> { GetAllPreviousTimeUseCase(repository = get()) }
        single<ClearTimeTableInDbUseCase> { ClearTimeTableInDbUseCase(repository = get()) }
    }
}
