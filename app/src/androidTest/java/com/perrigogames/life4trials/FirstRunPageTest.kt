package com.perrigogames.life4trials

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.perrigogames.life4trials.activity.firstrun.FirstRunInfoActivity
import com.perrigogames.life4trials.page.FirstRunPage
import com.perrigogames.life4trials.page.ReturningUserPopup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FirstRunPageTest {

    private val page = FirstRunPage
    private val popup = ReturningUserPopup

    @get:Rule val activityRule = ActivityTestRule(FirstRunInfoActivity::class.java)

    @Test
    fun pageAppears() {
        onView(page.titleLabel).check(matches(isDisplayed()))
        onView(page.brandImage).check(matches(isDisplayed()))
        onView(page.greetingsLabel).check(matches(isDisplayed()))
        onView(page.nameField).check(matches(isDisplayed()))
        onView(page.rivalField).check(matches(isDisplayed()))
        onView(page.twitterField).check(matches(isDisplayed()))
        onView(page.placementRadio).check(matches(isDisplayed()))
        onView(page.selectRankRadio).check(matches(isDisplayed()))
        onView(page.noRankRadio).check(matches(isDisplayed()))
        onView(page.signInButton).check(matches(isDisplayed()))
    }

    @Test
    fun testUserExists() {
        onView(page.nameField).perform(typeText("KONNOR\n"))
        onView(popup.nameLabel).waitUntilVisible().check(matches(isDisplayed()))
        onView(popup.rivalLabel).check(matches(isDisplayed()))
        onView(popup.twitterLabel).check(matches(isDisplayed()))
        onView(popup.rankImage).check(matches(isDisplayed()))
    }
}