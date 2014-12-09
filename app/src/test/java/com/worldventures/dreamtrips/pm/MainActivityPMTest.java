package com.worldventures.dreamtrips.pm;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.view.presentation.activity.MainActivityPresentation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class MainActivityPMTest {

    private DataManager dataManager;

    @Before
    public void setUp() throws Exception {
        DTApplication app = (DTApplication) Robolectric.application;
        dataManager = new DataManager(app);
    }

    public void testTrips() throws Exception {
        CountDownLatch signal = new CountDownLatch(1);
        MainActivityPresentation map = new MainActivityPresentation(new MainActivityPresentation.View() {
            @Override
            public void tripsLoaded() {
                signal.countDown();
            }
        }, dataManager);
        map.loadTrips();
        signal.await(10, TimeUnit.SECONDS);
        Assert.assertFalse("Trips exist", map.getTrips().isEmpty());
    }
}
