package com.lksnext.parking;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lksnext.parking.view.activity.LoginActivity;
import com.lksnext.parking.view.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public  ActivityScenarioRule<LoginActivity> rule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginFailNoEmail() {
        onView(withId(R.id.login_email_text)).perform(typeText(""));
        onView(withId(R.id.login_password_text)).perform(typeText("111111"));
        onView(withId(R.id.loginButton)).perform(click());

        //Check si sigue en el login (ha fallado la autenticación)
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));

    }

    @Test
    public void testLoginFailInvalidEmail() {
        onView(withId(R.id.login_email_text)).perform(typeText("unemailinvalido.com"));
        onView(withId(R.id.login_password_text)).perform(typeText("111111"));
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));

    }

    @Test
    public void testLoginFailNoPassword() {
        onView(withId(R.id.login_email_text)).perform(typeText("martinsaski@gmail.com"));
        onView(withId(R.id.login_password_text)).perform(typeText(""));
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginFailInvalidPassword() {
        onView(withId(R.id.login_email_text)).perform(typeText("martinsaski@gmail.com"));
        onView(withId(R.id.login_password_text)).perform(typeText("qqqqqqqq"));
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginSuccess() {
        Intents.init();

        onView(withId(R.id.login_email_text)).perform(typeText("martinsaski@gmail.com"));
        onView(withId(R.id.login_password_text)).perform(typeText("111111"));
        onView(withId(R.id.loginButton)).perform(click());

        SystemClock.sleep(2000);

        intended(hasComponent(MainActivity.class.getName()));

        Intents.release();
    }
}
