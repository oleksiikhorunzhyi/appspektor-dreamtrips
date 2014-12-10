package com.worldventures.dreamtrips.pm;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.view.presentation.activity.MainActivityPresentation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class MainActivityPMTest {

    private DataManager dataManager;

    @Before
    public void setUp() throws Exception {
        DTApplication app = (DTApplication) Robolectric.application;
        dataManager = new DataManager(app);
    }

    @Test
    public void testTrips() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        MainActivityPresentation map = new MainActivityPresentation(new MainActivityPresentation.View() {
            @Override
            public void tripsLoaded() {
                signal.countDown();
            }
        }, dataManager);
        signal.await(10, TimeUnit.SECONDS);
        Assert.assertTrue("Trips not exist", map.getTrips().isEmpty());
    }
}
