package com.worldventures.dreamtrips.api.tests.smoke.smart_card;

import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.smart_card.availability_card.AvailabilitySmartCardHttpAction;
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.GetTermsAndConditionsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions;
import com.worldventures.dreamtrips.api.smart_card.user_association.AssociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Features("SmartCard")
public class AvailabilitySmartCardTest extends BaseTestWithSession {

    @Fixture("alternative_card_user_data")
    AssociationCardUserData defaultCrdUserData;

    @Fixture("update_card_user_data")
    UpdateCardUserData updateCardUserData;

    private TermsAndConditions termsAndConditions;

    @BeforeClass
    void tryDisassociateFirst() {
        SafeExecutor<AvailabilitySmartCardTest> safeExecutor = SafeExecutor.from(this);
        safeExecutor.execute(new DisassociateCardUserHttpAction(defaultCrdUserData.scid(), defaultCrdUserData.deviceId()));
    }

    @Test
    void testAvailabilitySmartCardBeforeAssociate() {
        AvailabilitySmartCardHttpAction action = new AvailabilitySmartCardHttpAction(String.valueOf(defaultCrdUserData.scid()));
        execute(action);
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test
    void testGetTermsAndConditions() {
        GetTermsAndConditionsHttpAction action = execute(new GetTermsAndConditionsHttpAction());
        assertThat(action.statusCode()).isEqualTo(200);
        termsAndConditions = action.response();
        assertThat(termsAndConditions.url()).isNotEmpty();
        assertThat(termsAndConditions.version()).isGreaterThan(0);
    }

    @Test(dependsOnMethods = "testGetTermsAndConditions")
    void testAssociateCardUser() {
        AssociationCardUserData cardUserData = ImmutableAssociationCardUserData
                .copyOf(defaultCrdUserData)
                .withAcceptedTermsAndConditionVersion(termsAndConditions.version());

        AssociateCardUserHttpAction action = execute(new AssociateCardUserHttpAction(cardUserData));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();

        ServerUtil.waitForServerLag(SECONDS.toMillis(5));
    }

    @BeforeClass
    void prepareCardUserDataForUpdate() {
        updateCardUserData = ImmutableUpdateCardUserData.builder()
                .from(updateCardUserData)
                .nameToDisplay(updateCardUserData.nameToDisplay() + "_" + System.currentTimeMillis())
                .build();
    }

    @Test(dependsOnMethods = {"testGetTermsAndConditions", "testAssociateCardUser"})
    void testUpdateCardUser() {
        UpdateCardUserHttpAction action = execute(new UpdateCardUserHttpAction(defaultCrdUserData.scid(), updateCardUserData));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = {"testGetTermsAndConditions", "testAssociateCardUser", "testUpdateCardUser"})
    void testAvailabilitySmartCardAfterAssociate() {
        AvailabilitySmartCardHttpAction action = new AvailabilitySmartCardHttpAction(String.valueOf(defaultCrdUserData.scid()));
        execute(action);
        assertThat(action.statusCode()).isEqualTo(400);
    }

    @Test(dependsOnMethods = {"testGetTermsAndConditions", "testAssociateCardUser", "testUpdateCardUser", "testAvailabilitySmartCardAfterAssociate"})
    void testDisassociateCardUser() {
        DisassociateCardUserHttpAction action = execute(new DisassociateCardUserHttpAction(
                defaultCrdUserData.scid(), defaultCrdUserData.deviceId()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = {"testGetTermsAndConditions", "testAssociateCardUser", "testUpdateCardUser", "testAvailabilitySmartCardAfterAssociate", "testDisassociateCardUser"})
    void testAvailabilitySmartCardAfterDisassociate() {
        AvailabilitySmartCardHttpAction action = new AvailabilitySmartCardHttpAction(String.valueOf(defaultCrdUserData.scid()));
        execute(action);
        assertThat(action.statusCode()).isEqualTo(204);
    }
}
