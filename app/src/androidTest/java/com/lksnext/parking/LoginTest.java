package com.lksnext.parking;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lksnext.parking.view.activity.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLogin() {

        onView(withId(R.id.login_email_text)).perform(typeText(""));
        onView(withId(R.id.login_password_text)).perform(typeText("111111"));
        onView(withId(R.id.loginButton)).perform(click());

        //Check si sigue en el login (ha fallado la autenticación)
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));

    }
}
