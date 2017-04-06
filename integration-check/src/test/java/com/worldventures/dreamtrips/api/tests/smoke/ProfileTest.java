package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.facility.QRCodeHelper;
import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.profile.GetCurrentUserAddressHttpAction;
import com.worldventures.dreamtrips.api.profile.GetCurrentUserProfileHttpAction;
import com.worldventures.dreamtrips.api.profile.GetPublicUserProfileHttpAction;
import com.worldventures.dreamtrips.api.profile.GetUserAvatarHttpAction;
import com.worldventures.dreamtrips.api.profile.UpdateProfileAvatarHttpAction;
import com.worldventures.dreamtrips.api.profile.UpdateProfileBackgroundPhotoHttpAction;
import com.worldventures.dreamtrips.api.profile.model.AddressType;
import com.worldventures.dreamtrips.api.profile.model.PrivateUserProfile;
import com.worldventures.dreamtrips.api.profile.model.ProfileAddress;
import com.worldventures.dreamtrips.api.profile.model.PublicUserProfile;
import com.worldventures.dreamtrips.api.profile.model.UserAvatar;
import com.worldventures.dreamtrips.api.session.model.Account;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Profile")
public class ProfileTest extends BaseTestWithSession {

    public static final String GROUP_PROFILE = "profile";
    public static final String GROUP_AVATAR = "avatar";

    @Fixture("user_zh_hk")
    UserCredential anotherUser;

    @Fixture("user_with_address")
    UserCredential userWithAddress;

    @Test(groups = GROUP_PROFILE)
    void testGetCurrentUserProfile() {
        PrivateUserProfile profile = execute(new GetCurrentUserProfileHttpAction()).response();
        assertThat(profile).isNotNull();
        assertThat(profile.username()).isEqualTo(authorizedUser().username());
        assertThat(profile.id()).isNotNull();
        
        // compare PrivateUserProfile with account profile from session
        assertThat(session().user().equals(profile));
    }

    @Test(groups = GROUP_PROFILE)
    void testGetPublicUserProfile() {
        // get another user
        Account user = as(anotherUser).getSession().user();
        // get public profile of another user from default session account
        GetPublicUserProfileHttpAction profileAction = execute(new GetPublicUserProfileHttpAction(user.id()));
        PublicUserProfile profile = profileAction.response();

        assertThat(profile).isNotNull();
        assertThat(profile.username()).isEqualTo(anotherUser.username());

        // compare public profile with account profile from another user session
        assertThat(profile.equals(user));
    }

    @Test(groups = GROUP_PROFILE)
    void testUserAvatar()
    {
        GetUserAvatarHttpAction action = execute(new GetUserAvatarHttpAction(session().user().username()));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();

        UserAvatar userAvatar = action.response();
        assertThat(userAvatar.id()).isEqualTo(session().user().id());
        assertThat(userAvatar.avatar().equals(session().user().avatar()));
    }

    @Test(groups = GROUP_AVATAR, dependsOnGroups = GROUP_PROFILE)
    void testAvatarUploading() throws IOException {
        final String imageKey = UUID.randomUUID().toString();
        File file = QRCodeHelper.createQRCode(imageKey);

        // should use another user not to affect all the test suite
        UpdateProfileAvatarHttpAction action = as(anotherUser).execute(new UpdateProfileAvatarHttpAction(file));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();

        final String originalURL = action.response().avatar().original();
        String receivedKey = QRCodeHelper.readQRCodeFromURLWithAssertion(originalURL);

        assertThat(receivedKey).isEqualTo(imageKey);
    }

    @Test(groups = GROUP_AVATAR, dependsOnGroups = GROUP_PROFILE)
    void testBackgroundPhotoUploading() throws IOException {
        final String imageKey = UUID.randomUUID().toString();
        File file = QRCodeHelper.createQRCode(imageKey);

        // should use another user not to affect all the test suite
        UpdateProfileBackgroundPhotoHttpAction action = as(anotherUser).execute(new UpdateProfileBackgroundPhotoHttpAction(file));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();

        final String originalURL = action.response().backgroundPhotoUrl();
        String receivedKey = QRCodeHelper.readQRCodeFromURLWithAssertion(originalURL);

        assertThat(receivedKey).isEqualTo(imageKey);
    }

    @Test(groups = GROUP_PROFILE)
    void testGetAddress() {
        GetCurrentUserAddressHttpAction action = as(userWithAddress).execute(new GetCurrentUserAddressHttpAction(AddressType.BILLING));
        assertThat(action.response()).isNotNull();
        assertThat(action.response()).flatExtracting(ProfileAddress::type).containsOnly(AddressType.BILLING);
    }
}
