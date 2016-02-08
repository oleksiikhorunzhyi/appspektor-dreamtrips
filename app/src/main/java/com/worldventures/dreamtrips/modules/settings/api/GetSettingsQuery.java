package com.worldventures.dreamtrips.modules.settings.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.settings.model.SettingsHolder;

public class GetSettingsQuery extends Query<SettingsHolder> {

    public GetSettingsQuery() {
        super(SettingsHolder.class);
    }

    @Override
    public SettingsHolder loadDataFromNetwork() throws Exception {
        return getService().getSettings();
    }
}
