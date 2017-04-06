package com.worldventures.dreamtrips.api.settings;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.settings.model.SettingsBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;

@HttpAction(value = "/api/user/settings", method = HttpAction.Method.PATCH)
public class UpdateSettingsHttpAction extends AuthorizedHttpAction {

    @Body
    public final SettingsBody body;

    public UpdateSettingsHttpAction(SettingsBody body) {
        this.body = body;
    }
}
