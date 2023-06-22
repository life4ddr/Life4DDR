package com.perrigogames.life4.android

//import androidx.test.espresso.*
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import java.util.concurrent.TimeoutException
//
//fun ViewInteraction.waitUntilVisible(timeout: Long = 5000): ViewInteraction {
//    val startTime = System.currentTimeMillis()
//    val endTime = startTime + timeout
//
//    do {
//        try {
//            check(matches(isDisplayed()))
//            return this
//        } catch (e: NoMatchingViewException) {
//            Thread.sleep(50)
//        }
//    } while (System.currentTimeMillis() < endTime)
//    throw TimeoutException()
//}