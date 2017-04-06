package com.worldventures.dreamtrips.api.tests.smoke.smart_card;

import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.smart_card.availability_card.AvailabilitySmartCardHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.CreateSmartCardLocationHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.GetSmartCardLocationsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocationBody;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationBody;
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.GetTermsAndConditionsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions;
import com.worldventures.dreamtrips.api.smart_card.user_association.AssociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationCardUserData;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Features("SmartCard")
public class SmartCardLocationTest extends BaseTestWithSession {

    @Fixture("alternative_card_user_data")
    AssociationCardUserData associationCardUserData;

    @Fixture("smart_card_location")
    SmartCardLocation smartCardLocation;

    SmartCardLocationBody smartCardLocationBody;

    @BeforeClass
    void prepareUserCardAssociation() {
        associationCardUserData = ImmutableAssociationCardUserData.builder()
                .from(associationCardUserData)
                .build();
    }

    private TermsAndConditions termsAndConditions;

    @BeforeClass
    void tryDisassociateFirst() {
        SafeExecutor<SmartCardLocationTest> safeExecutor = SafeExecutor.from(this);
        safeExecutor.execute(new DisassociateCardUserHttpAction(associationCardUserData.scid(), associationCardUserData.deviceId()));
    }

    @Test
    void testAvailabilitySmartCardBeforeAssociate() {
        AvailabilitySmartCardHttpAction action = new AvailabilitySmartCardHttpAction(String.valueOf(associationCardUserData.scid()));
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
                .copyOf(associationCardUserData)
                .withAcceptedTermsAndConditionVersion(termsAndConditions.version());

        AssociateCardUserHttpAction action = execute(new AssociateCardUserHttpAction(cardUserData));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();

        ServerUtil.waitForServerLag(SECONDS.toMillis(5));
    }

    @BeforeClass
    void prepareSmartCardLocationModel() {
        smartCardLocation = ImmutableSmartCardLocation.builder()
                .from(smartCardLocation)
                .createdAt(Calendar.getInstance().getTime())
                .build();
        smartCardLocationBody = ImmutableSmartCardLocationBody.builder()
                .locations(Collections.singletonList(smartCardLocation))
                .build();
    }

    @Test(dependsOnMethods = {"testGetTermsAndConditions", "testAssociateCardUser"})
    void testLocationCreation() {
        CreateSmartCardLocationHttpAction action = execute(
                new CreateSmartCardLocationHttpAction(associationCardUserData.scid(), smartCardLocationBody));
        assertThat(action.statusCode()).isEqualTo(204);
    }


    @Test(dependsOnMethods = {"testGetTermsAndConditions", "testAssociateCardUser", "testLocationCreation"})
    void testLocationGet() {
        GetSmartCardLocationsHttpAction action = execute(new GetSmartCardLocationsHttpAction(associationCardUserData.scid()));
        assertThat(action.statusCode()).isEqualTo(200);
        List<SmartCardLocation> locationResponse = action.response();
        assertThat(locationResponse).isNotNull();
        assertThat(locationResponse).isNotEmpty();
        assertThat(locationResponse.get(0)).isNotNull();
    }
}
