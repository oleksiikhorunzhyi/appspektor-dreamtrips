package com.worldventures.dreamtrips.pm;

import com.worldventures.dreamtrips.view.presentation.activity.MainActivityPresentation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivityPMTest extends BasePMTest {

    public void testTrips() throws Exception {
        CountDownLatch signal = new CountDownLatch(1);
        MainActivityPresentation map = new MainActivityPresentation(signal::countDown, getDataManager());
        map.loadTrips();
        signal.await(10, TimeUnit.SECONDS);
        assertFalse("Trips exist", map.getTrips().isEmpty());
    }
}
