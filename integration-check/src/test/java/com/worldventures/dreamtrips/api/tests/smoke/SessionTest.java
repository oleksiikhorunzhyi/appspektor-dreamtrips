package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.http.executor.ActionExecutor;
import com.worldventures.dreamtrips.api.http.executor.BaseActionExecutor;
import com.worldventures.dreamtrips.api.http.provider.AuthorizedJanetProvider;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.api.session.LogoutHttpAction;
import com.worldventures.dreamtrips.api.session.model.Session;
import com.worldventures.dreamtrips.api.tests.BaseTest;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features("Session")
public class SessionTest extends BaseTest {

    @Fixture("default_user")
    UserCredential defaultUser;
    @Fixture("invalid_user")
    UserCredential invalidUser;

    @Override
    protected ActionExecutor provideActionExecutor() {
        return new BaseActionExecutor<>(new AuthorizedJanetProvider());
    }

    @Test
    void testSuccessfulLogin() {
        LoginHttpAction action = execute(new LoginHttpAction(defaultUser.username(), defaultUser.password()));
        Session session = action.response();

        assertThat(session).isNotNull();
        assertThat(session.token()).isNotNull();
        assertThat(session.ssoToken()).isNotNull().isNotEmpty();
        assertThat(session.locale()).isNotEmpty();
        assertThat(session.permissions()).isNotNull().isNotEmpty();
        assertThat(session.user()).isNotNull();
    }

    @Test(dependsOnMethods = "testLogout")
    void testInvalidLogin() {
        LoginHttpAction action = execute(new LoginHttpAction(invalidUser.username(), invalidUser.password()));

        assertThat(action.statusCode()).isEqualTo(422);
        assertThat(action.errorResponse()).isNotNull();
        assertThat(action.errorResponse().errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = "testSuccessfulLogin")
    void testLogout() {
        LogoutHttpAction action = execute(new LogoutHttpAction());

        assertThat(action.statusCode()).isEqualTo(204);
    }
}
