package com.worldventures.dreamtrips.modules.settings.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.settings.model.Settings;

import java.util.List;

public class SettingsManager {

    public List<Settings> merge(List<Settings> fromServer, List<Settings> local) {
        return Queryable.from(fromServer).filter(local::contains).toList();
    }
}
