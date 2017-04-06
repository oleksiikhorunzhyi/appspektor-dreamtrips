package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.settings.GetSettingsHttpAction;
import com.worldventures.dreamtrips.api.settings.UpdateSettingsHttpAction;
import com.worldventures.dreamtrips.api.settings.model.ImmutableSettingsBody;
import com.worldventures.dreamtrips.api.settings.model.SettingsBody;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features("Settings")
public class SettingsTest extends BaseTestWithSession {

    @Test
    void testGetSettings() {
        GetSettingsHttpAction action = execute(new GetSettingsHttpAction());
        assertThat(action.response()).isNotEmpty();
    }

    @Test
    void testUpdateSettings() {
        // prepare settings to update
        GetSettingsHttpAction getAction = execute(new GetSettingsHttpAction());
        SettingsBody settingsToUpdate = ImmutableSettingsBody.builder().settings(getAction.response()).build();
        // execute and check
        UpdateSettingsHttpAction updateAction = execute(new UpdateSettingsHttpAction(settingsToUpdate));
        assertThat(updateAction.statusCode()).isEqualTo(204);
    }

}
