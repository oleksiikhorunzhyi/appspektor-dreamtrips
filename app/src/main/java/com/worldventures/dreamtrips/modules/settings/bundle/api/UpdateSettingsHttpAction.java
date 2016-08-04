package com.worldventures.dreamtrips.modules.settings.bundle.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;

@HttpAction(value = "/api/user/settings", method = HttpAction.Method.PATCH)
public class UpdateSettingsHttpAction extends AuthorizedHttpAction {

    @Body SettingsHolder settingsHolder;

    public UpdateSettingsHttpAction(List<Setting> settings) {
        settingsHolder = new SettingsHolder(settings);
    }

    private class SettingsHolder {

        private final List<Setting> settings;

        public SettingsHolder(List<Setting> settings) {
            this.settings = settings;
        }
    }
}