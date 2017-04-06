package com.worldventures.dreamtrips.api.tests.smoke.smart_card;

import com.worldventures.dreamtrips.api.facility.QRCodeHelper;
import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.http.provider.SystemEnvProvider;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.api.smart_card.association_info.GetAssociatedCardsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.association_info.GetCardDataHttpAction;
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.GetTermsAndConditionsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions;
import com.worldventures.dreamtrips.api.smart_card.user_association.AssociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.GetCompatibleDevicesHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.AssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;
import com.worldventures.dreamtrips.api.uploadery.UploadSmartCardImageHttpAction;

import net.glxn.qrgen.core.image.ImageType;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Features("SmartCard")
public class CardUserTest extends BaseTestWithSession {

    final private static int ORIGINAL_SIZE = 500;
    final private static ImageType IMAGE_TYPE = ImageType.PNG;


    ///////////////////////////////////////////////////////////////////////////
    // Association/Disassociation
    ///////////////////////////////////////////////////////////////////////////

    @Fixture("card_user_data")
    AssociationCardUserData defaultCrdUserData;

    private TermsAndConditions termsAndConditions;

    @BeforeClass
    void tryDisassociateFirst() {
        SafeExecutor<CardUserTest> safeExecutor = SafeExecutor.from(this);
        safeExecutor.execute(new DisassociateCardUserHttpAction(defaultCrdUserData.scid(), defaultCrdUserData.deviceId()));
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

    @Test(dependsOnMethods = "testAssociateCardUser")
    void testUploadImageryAvatar() throws IOException {
        String qrCode = UUID.randomUUID().toString();
        File qrCodeImage = QRCodeHelper.createSizedQRCode(qrCode, ORIGINAL_SIZE, ORIGINAL_SIZE, IMAGE_TYPE);
        String smartCardId = String.valueOf(defaultCrdUserData.scid());
        String userId = authorizedUser().username();
        String baseUrl = new SystemEnvProvider().provide().apiUploaderyUrl();

        UploadSmartCardImageHttpAction action = execute(new UploadSmartCardImageHttpAction(baseUrl, smartCardId, userId, qrCodeImage));
        assertThat(action.statusCode()).isEqualTo(200);
    }

    @Test(dependsOnMethods = {"testAssociateCardUser", "testUpdateCardUser"})
    void testGetCardData() {
        GetCardDataHttpAction action = execute(new GetCardDataHttpAction(defaultCrdUserData.scid()));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();
    }

    @Test(dependsOnMethods = {"testAssociateCardUser", "testUpdateCardUser"})
    void testGetAssociatedCards() {
        GetAssociatedCardsHttpAction action = execute(new GetAssociatedCardsHttpAction(defaultCrdUserData.deviceId()));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotEmpty();
    }

    @Test(dependsOnMethods = {"testAssociateCardUser", "testUpdateCardUser", "testGetAssociatedCards"})
    void testDisassociateCardUser() {
        DisassociateCardUserHttpAction action = execute(new DisassociateCardUserHttpAction(
                defaultCrdUserData.scid(), defaultCrdUserData.deviceId()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Card User Update
    ///////////////////////////////////////////////////////////////////////////

    @Fixture("update_card_user_data")
    UpdateCardUserData updateCardUserData;
    @Fixture("new_update_card_user_data")
    UpdateCardUserData newUpdateCardUserData;

    @BeforeClass
    void prepareCardUserDataForUpdate() {
        updateCardUserData = ImmutableUpdateCardUserData.builder()
                .from(updateCardUserData)
                .nameToDisplay(updateCardUserData.nameToDisplay() + "_" + System.currentTimeMillis())
                .build();
    }

    @Test(dependsOnMethods = "testAssociateCardUser")
    void testUpdateCardUser() {
        UpdateCardUserHttpAction action = execute(new UpdateCardUserHttpAction(defaultCrdUserData.scid(), updateCardUserData));
        assertThat(action.statusCode()).isEqualTo(204);

        action = execute(new UpdateCardUserHttpAction(defaultCrdUserData.scid(), newUpdateCardUserData));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Compatible devices
    ///////////////////////////////////////////////////////////////////////////

    @Test
    void testGetCompatibleDevices() {
        List<Device> devices = execute(new GetCompatibleDevicesHttpAction(1, 10)).response();
        assertThat(devices).isNotEmpty();
    }

}
