package com.example.gazpromtest.domain.useCase

import com.example.gazpromtest.domain.repository.MainRepository
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val repository: MainRepository
) {

    suspend operator fun invoke() = repository.getCities()

}