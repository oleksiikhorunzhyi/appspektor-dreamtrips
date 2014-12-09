package com.worldventures.dreamtrips.pm;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.view.presentation.activity.LoginActivityPresentation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityPMTest {

    private DataManager dataManager;

    @Before
    public void setUp() throws Exception {
        DTApplication app = (DTApplication) Robolectric.application;
        dataManager = new DataManager(app);
    }

    public void testInputFields() {
        LoginActivityPresentation lap = new LoginActivityPresentation(new LoginActivityPresentation.View() {
            @Override
            public void openMainWindow() {
            }
        }, dataManager);
        lap.setUsername("John");
        lap.setUserPassword("Password");
        Assert.assertTrue(lap.getUsername() != null && !lap.getUsername().isEmpty());
        Assert.assertTrue(lap.getUserPassword() != null && !lap.getUserPassword().isEmpty());
    }

    public void testLogin() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        LoginActivityPresentation.View callback = new LoginActivityPresentation.View() {
            @Override
            public void openMainWindow() {
                signal.countDown();
                Assert.assertTrue("All is good", true);
            }
        };
        LoginActivityPresentation lap = new LoginActivityPresentation(callback, dataManager);
        lap.setUsername("John");
        lap.setUserPassword("Password");
        lap.loginAction();
        signal.await();
    }
}
