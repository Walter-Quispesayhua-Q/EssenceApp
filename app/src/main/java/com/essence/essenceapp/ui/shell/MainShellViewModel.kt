package com.essence.essenceapp.ui.shell

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.essence.essenceapp.core.network.storage.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class MainShellViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        refreshAuthState()
    }

    fun refreshAuthState() {
        viewModelScope.launch {
            val token = tokenManager.token.first()
            val userId = runCatching { tokenManager.getUserId() }.getOrNull()

            Log.e(
                "AUTH_DEBUG",
                "refreshAuthState tokenPresent=${!token.isNullOrBlank()} userId=$userId"
            )

            _isLoggedIn.value = !token.isNullOrBlank()
        }
    }
}