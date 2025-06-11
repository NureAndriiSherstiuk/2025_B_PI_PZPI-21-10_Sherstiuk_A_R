package com.example.fliplearn_final.presentation.pages.test_2

sealed class Test2Event {
    data class SelectAnswer(val selectedOption: String) : Test2Event()
    object Skip : Test2Event()
    object Restart : Test2Event()
}
