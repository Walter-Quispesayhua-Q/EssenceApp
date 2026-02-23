package com.essence.essenceapp.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
): ViewModel() {
    private val _home = MutableStateFlow<Home?>(null)
    val home: StateFlow<Home?> = _home

    fun loadHome() {
        viewModelScope.launch {
           try {
               val result = homeRepository.getHome()
               _home.value = result
           } catch (e: Exception) {
               _home.value = null
           }
        }
    }
}