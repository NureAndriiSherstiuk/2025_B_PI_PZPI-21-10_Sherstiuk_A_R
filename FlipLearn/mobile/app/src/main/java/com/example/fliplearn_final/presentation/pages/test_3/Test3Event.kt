package com.example.fliplearn_final.presentation.pages.test_3


sealed interface Test3Event {
    data class EnteredTranslation(val value: String) : Test3Event
    object SubmitAnswer : Test3Event
    object SkipAnswer : Test3Event
    object RestartTest : Test3Event
    object FinishTest : Test3Event
}
