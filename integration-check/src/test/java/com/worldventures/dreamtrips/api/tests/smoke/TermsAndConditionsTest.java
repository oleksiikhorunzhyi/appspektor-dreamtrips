package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.terms_and_conditions.AcceptTermsAndConditionsHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static ie.corballis.fixtures.assertion.FixtureAssert.assertThat;

@Features("Session")
public class TermsAndConditionsTest extends BaseTestWithSession {

    @Test
    void testTermsAndConditionsAccepting() {
        AcceptTermsAndConditionsHttpAction action = new AcceptTermsAndConditionsHttpAction("test");

        execute(action);

        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test
    void testEmptyTermsAndConditionsAccepting() {
        AcceptTermsAndConditionsHttpAction action = new AcceptTermsAndConditionsHttpAction("");

        execute(action);

        assertThat(action.statusCode()).isEqualTo(422);
    }
}
