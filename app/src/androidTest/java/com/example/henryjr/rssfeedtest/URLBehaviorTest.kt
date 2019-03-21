package com.example.henryjr.rssfeedtest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.not
import androidx.recyclerview.widget.RecyclerView

@RunWith(AndroidJUnit4::class)
@LargeTest
class URLBehaviorTest {
    private lateinit var urlToBetypedValid: String
    private lateinit var urlToBetypedNotValid: String

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @Before
    fun initValidURL() {
        // Specify a valid string.
        urlToBetypedValid = "https://www.gsmarena.com/rss-news-reviews.php3"
    }

    @Before
    fun initNotValidURL() {
        urlToBetypedNotValid = "http://sample"
    }

    fun getRVcount(): Int {
        val recyclerView = activityRule.activity.findViewById(R.id.recyclerView) as RecyclerView
        return recyclerView.adapter!!.itemCount
    }

    @Test
    fun inputURLValid() {
        // Type text and then press the button.
        onView(withId(R.id.rssFeedEditText))
                .perform(typeText(urlToBetypedValid), closeSoftKeyboard())
        onView(withId(R.id.fetchFeedButton)).perform(click())

        assert(getRVcount() > 0)
    }

    @Test
    fun inputURLInvalid() {
        // Type text and then press the button.
        onView(withId(R.id.rssFeedEditText))
                .perform(typeText(urlToBetypedNotValid), closeSoftKeyboard())
        onView(withId(R.id.fetchFeedButton)).perform(click())

        // toast should be visible
        onView(withText(R.string.invalid_rss_url)).inRoot(withDecorView(
                not(activityRule.activity.window.decorView))).check(matches(isDisplayed()));
    }

    @Test
    fun inputURLEmpty() {
        // Type text and then press the button.
        onView(withId(R.id.rssFeedEditText))
                .perform(clearText())
        onView(withId(R.id.fetchFeedButton)).perform(click())

        // toast should be visible
        onView(withText(R.string.invalid_rss_url)).inRoot(withDecorView(
                not(activityRule.activity.window.decorView))).check(matches(isDisplayed()));
    }
}