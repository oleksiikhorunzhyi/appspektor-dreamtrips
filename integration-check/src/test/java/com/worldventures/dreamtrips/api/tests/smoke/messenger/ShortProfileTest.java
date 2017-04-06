package com.worldventures.dreamtrips.api.tests.smoke.messenger;


import com.worldventures.dreamtrips.api.messenger.GetShortProfileHttpAction;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Profile")
public class ShortProfileTest extends BaseTestWithSession {

    @DataProvider(name = "usernames")
    static Object[][] provideUsernames() {
        return new Object[][]{{Arrays.asList("65663832", "65663844", "3285301", "fshikalgar")}};
    }

    @Test(dataProvider = "usernames")
    void testShortProfile(List<String> usernames) {
        GetShortProfileHttpAction action = execute(new GetShortProfileHttpAction(usernames));

        List<ShortUserProfile> shortUserProfiles = action.getShortUsers();
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(shortUserProfiles).as("check if short user profile list is valid")
                .isNotNull()
                .hasSize(4)
                .doesNotContainNull();
        assertThat(shortUserProfiles).as("Check different ids")
                .extracting("id").doesNotHaveDuplicates();
    }

}
