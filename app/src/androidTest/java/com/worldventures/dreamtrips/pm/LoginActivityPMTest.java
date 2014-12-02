package com.worldventures.dreamtrips.pm;

import com.worldventures.dreamtrips.view.presentation.activity.LoginActivityPresentation;

import java.util.concurrent.CountDownLatch;

public class LoginActivityPMTest extends BasePMTest {

    public void testInputFields() {
        LoginActivityPresentation lap = new LoginActivityPresentation(() -> {
        }, getDataManager());
        lap.setUsername("John");
        lap.setUserPassword("Password");
        assertTrue(lap.getUsername() != null && !lap.getUsername().isEmpty());
        assertTrue(lap.getUserPassword() != null && !lap.getUserPassword().isEmpty());
    }

    public void testLogin() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);
        LoginActivityPresentation.View callback = () -> {
            signal.countDown();
            assertTrue("All is good", true);
        };
        LoginActivityPresentation lap = new LoginActivityPresentation(callback, getDataManager());
        lap.setUsername("John");
        lap.setUserPassword("Password");
        lap.loginAction();
        signal.await();
    }
}
