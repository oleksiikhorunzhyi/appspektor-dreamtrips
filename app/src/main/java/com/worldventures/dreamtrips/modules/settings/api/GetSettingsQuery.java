package com.worldventures.dreamtrips.modules.settings.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.settings.model.Settings;

import java.util.ArrayList;

public class GetSettingsQuery extends Query<ArrayList<Settings>> {

    public GetSettingsQuery() {
        super((Class<ArrayList<Settings>>) new ArrayList<Settings>().getClass());
    }

    @Override
    public ArrayList<Settings> loadDataFromNetwork() throws Exception {
        return getService().getSettings();
    }
}
