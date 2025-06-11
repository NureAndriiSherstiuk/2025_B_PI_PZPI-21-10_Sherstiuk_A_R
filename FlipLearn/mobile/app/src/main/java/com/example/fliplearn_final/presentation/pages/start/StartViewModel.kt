package com.example.fliplearn_final.presentation.pages.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.data.remote.auth.GoogleAuthClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val googleAuthClient: GoogleAuthClient,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isSignedIn = MutableStateFlow<Boolean?>(null)
    val isSignedIn: StateFlow<Boolean?> = _isSignedIn

    fun checkIfAlreadySignedIn() {
        viewModelScope.launch {
            val isLocallySignedIn = userPreferences.isLocallySignedIn.first()
            val isGoogleSignedIn = googleAuthClient.isSignedIn()
            _isSignedIn.value = isLocallySignedIn || isGoogleSignedIn
        }
    }
}

