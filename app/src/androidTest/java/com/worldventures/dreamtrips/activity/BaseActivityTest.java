package com.worldventures.dreamtrips.activity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

public class BaseActivityTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    protected Solo solo;

    public BaseActivityTest(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
