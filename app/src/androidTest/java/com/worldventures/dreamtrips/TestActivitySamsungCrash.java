package com.worldventures.dreamtrips;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.techery.spares.utils.SamsungCrashActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestActivitySamsungCrash {

    @Rule
    public ActivityTestRule<SamsungCrashActivity> mActivityRule = new ActivityTestRule(SamsungCrashActivity.class);

    /** See https://code.google.com/p/android/issues/detail?id=78377#c120 */
    @Test
    public void samsungAppCompat() {
        onView(allOf(withId(R.id.test_toolbar))).perform(click());
        assertTrue(true);
    }
}
