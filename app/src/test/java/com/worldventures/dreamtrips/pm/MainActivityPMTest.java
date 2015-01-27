package com.worldventures.dreamtrips.pm;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class MainActivityPMTest {

 /*   private DataManager dataManager;

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
        }, null);
        signal.await(10, TimeUnit.SECONDS);
        Assert.assertTrue("Trips not exist", map.getTrips().isEmpty());
    }*/
}
