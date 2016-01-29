package com.worldventures.dreamtrips.modules.settings.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.util.ArrayList;

public class GetSettingsQuery extends Query<ArrayList<Setting>> {

    public GetSettingsQuery() {
        super((Class<ArrayList<Setting>>) new ArrayList<Setting>().getClass());
    }

    @Override
    public ArrayList<Setting> loadDataFromNetwork() throws Exception {
        return getService().getSettings();
    }
}
