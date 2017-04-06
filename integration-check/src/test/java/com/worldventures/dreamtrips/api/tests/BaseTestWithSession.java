package com.worldventures.dreamtrips.api.tests;

import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.http.executor.AuthorizedActionExecutor;
import com.worldventures.dreamtrips.api.session.model.Session;

import org.testng.annotations.BeforeSuite;

import ie.corballis.fixtures.annotation.Fixture;


public class BaseTestWithSession extends BaseTest {

    @Fixture("default_user")
    UserCredential defaultUser;

    private static AuthorizedActionExecutor authorizedActionExecutor;

    @BeforeSuite
    public void prepareAuthorizedExecutor() throws Exception {
        injectFixtures();
        authorizedActionExecutor = as(defaultUser);
    }

    @Override
    protected AuthorizedActionExecutor provideActionExecutor() {
        return authorizedActionExecutor;
    }

    protected UserCredential authorizedUser() {
        return defaultUser;
    }

    protected Session session() {
        return authorizedActionExecutor.getSession();
    }
}
