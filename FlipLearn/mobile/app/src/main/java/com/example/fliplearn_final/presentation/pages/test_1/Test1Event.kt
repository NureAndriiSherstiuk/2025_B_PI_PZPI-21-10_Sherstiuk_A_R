package com.example.fliplearn_final.presentation.pages.test_1

sealed class Test1Event {
    data class SubmitAnswer(val isTrueSelected: Boolean) : Test1Event()
    object Skip : Test1Event()
    object Restart : Test1Event()
}