package com.lksnext.parking;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.google.android.apps.common.testing.accessibility.framework.replacements.Point;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parking.view.activity.LoginActivity;
import com.lksnext.parking.view.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BookingTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testBookSpot(){
        onView(withId(R.id.login_email_text)).perform(typeText("martinsaski@gmail.com"));
        onView(withId(R.id.login_password_text)).perform(typeText("111111"));
        onView(withId(R.id.loginButton)).perform(click());

        SystemClock.sleep(7000);


        onView(withId(R.id.add_button)).perform(click());
        SystemClock.sleep(2000);

        onView(withId(R.id.add_booking_button)).check(matches(isNotEnabled()));

        onView(withId(R.id.coche_chip)).perform(click());
        onView(withId(R.id.miercoles_chip)).perform(click());
        SystemClock.sleep(2000);

        onView(withId(R.id.hour_chip_10)).perform(click());
        onView(withId(R.id.hour_chip_14)).perform(click());
        SystemClock.sleep(2000);

        //Hacer scroll para que el RecyclerView sea visible
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Point centerOfScreen = new Point(device.getDisplayWidth() / 2, device.getDisplayHeight() / 2 + 200);
        device.swipe(centerOfScreen.getX(), centerOfScreen.getY(), centerOfScreen.getX(), 0, 20);
        SystemClock.sleep(2000);

        onView(withId(R.id.available_spots_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //El botón se debería habilitar al elegir la plaza
        onView(withId(R.id.add_booking_button)).check(matches(isEnabled()));

        onView(withId(R.id.add_booking_button)).perform(click());
    }

}
