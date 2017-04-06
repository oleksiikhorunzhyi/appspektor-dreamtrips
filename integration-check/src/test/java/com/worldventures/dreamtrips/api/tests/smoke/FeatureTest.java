package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.api.session.model.Feature;
import com.worldventures.dreamtrips.api.session.model.Session;
import com.worldventures.dreamtrips.api.tests.BaseTest;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Session", "Feature"})
public class FeatureTest extends BaseTest {

    ///////////////////////////////////////////////////////////////////////////
    // Wallet
    ///////////////////////////////////////////////////////////////////////////

    @Fixture("default_user")
    UserCredential walletUser;

    @Fixture("user_with_no_rds")
    UserCredential simpleUser;

    @Fixture("device_approved_for_wallet")
    Device walletSupportedDevice;

    @Fixture("device_rejected_for_wallet")
    Device walletUnsupportedDevice;

    @Test
    void testWalletFeatureApproved() {
        Session session = execute(
                new LoginHttpAction(walletUser.username(), walletUser.password())
        ).response();
        checkHasFeatures(session, Feature.FeatureName.WALLET);
    }

    @Test
    void testWalletFeatureDenied() {
        Session session = execute(
                new LoginHttpAction(simpleUser.username(), simpleUser.password())
        ).response();
        checkHasNoFeatures(session, Feature.FeatureName.WALLET);
    }

    @Test
    void testWalletFeatureApprovedByDevice() {
        LoginHttpAction action = new LoginHttpAction(walletUser.username(), walletUser.password(), walletSupportedDevice);
        action.setAppPlatformHeader("android-21");
        Session session = execute(action).response();
        checkHasFeatures(session, Feature.FeatureName.WALLET, Feature.FeatureName.WALLET_PROVISIONING);
    }

    @Test
    void testWalletFeatureDeniedByDevice() {
        LoginHttpAction action = new LoginHttpAction(walletUser.username(), walletUser.password(), walletUnsupportedDevice);
        action.setAppPlatformHeader("android-21");
        Session session = execute(action).response();
        checkHasFeatures(session, Feature.FeatureName.WALLET);
        checkHasNoFeatures(session, Feature.FeatureName.WALLET_PROVISIONING);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Utils
    ///////////////////////////////////////////////////////////////////////////

    private void checkHasFeatures(Session session, Feature.FeatureName... features) {
        assertThat(session).isNotNull();
        assertThat(session.permissions()).extracting(Feature::name).contains(features);
    }

    private void checkHasNoFeatures(Session session, Feature.FeatureName... features) {
        assertThat(session).isNotNull();
        assertThat(session.permissions()).extracting(Feature::name).doesNotContain(features);
    }
}
