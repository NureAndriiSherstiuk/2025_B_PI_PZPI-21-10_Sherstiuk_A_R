package com.example.fliplearn_final.util

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}
