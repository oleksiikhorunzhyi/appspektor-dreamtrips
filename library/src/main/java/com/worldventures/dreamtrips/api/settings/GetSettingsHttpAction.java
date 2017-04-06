package com.worldventures.dreamtrips.api.settings;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.settings.model.Setting;
import com.worldventures.dreamtrips.api.settings.model.SettingsBody;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/user/settings")
public class GetSettingsHttpAction extends AuthorizedHttpAction {

    @Response
    SettingsBody holder;

    public List<Setting> response() {
        return holder.settings();
    }
}
