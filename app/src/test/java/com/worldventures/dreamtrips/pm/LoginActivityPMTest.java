package com.worldventures.dreamtrips.pm;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LoginActivityPMTest {

/*    private DataManager dataManager;

    @Before
    public void setUp() throws Exception {
        DTApplication app = (DTApplication) Robolectric.application;
        dataManager = new DataManager(app);
    }

    @Test
    public void testInputFields() {
        LoginFragmentPresentation lap = new LoginFragmentPresentation(new LoginFragmentPresentation.View() {
            @Override
            public void openMainWindow() {
            }
        }, dataManager);
        lap.setUsername("John");
        lap.setUserPassword("Password");
        Assert.assertTrue(lap.getUsername() != null && !lap.getUsername().isEmpty());
        Assert.assertTrue(lap.getUserPassword() != null && !lap.getUserPassword().isEmpty());
    }

    @Test
    public void testLogin() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        LoginFragmentPresentation.View callback = new LoginFragmentPresentation.View() {
            @Override
            public void openMainWindow() {
                signal.countDown();
                Assert.assertTrue("All is good", true);
            }
        };
        LoginFragmentPresentation lap = new LoginFragmentPresentation(callback, dataManager);
        lap.setUsername("John");
        lap.setUserPassword("Password");
        lap.loginAction();
        signal.await();
    }*/
}
