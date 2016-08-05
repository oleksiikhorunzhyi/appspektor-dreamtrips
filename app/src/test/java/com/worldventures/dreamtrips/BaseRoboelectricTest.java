package com.worldventures.dreamtrips;

import android.content.Context;
import android.content.res.Resources;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class,
        constants = BuildConfig.class, shadows = ShadowMultiDex.class)
public class BaseRoboelectricTest {

    protected Context getContext() {
        return RuntimeEnvironment.application;
    }

    protected Resources getResources() {
        return getContext().getResources();
    }
}
