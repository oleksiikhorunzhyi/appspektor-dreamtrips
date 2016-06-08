package com.worldventures.dreamtrips.modules.common.api.janet;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.settings.model.SettingsHolder;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/user/settings", method = HttpAction.Method.GET)
public class GetAppSettingsHttpAction extends AuthorizedHttpAction {

    @Response
    SettingsHolder settingsHolder;

    public SettingsHolder getSettingsHolder() {
        return settingsHolder;
    }

}
