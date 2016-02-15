package com.worldventures.dreamtrips.modules.dtl.model;

import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

public enum DistanceType {
    MILES("ml"), KMS("km");

    String analyticsTypeName;

    DistanceType(String analyticsTypeName) {
        this.analyticsTypeName = analyticsTypeName;
    }

    public String getTypeNameForAnalytics() {
        return analyticsTypeName;
    }

    public static DistanceType provideFromSetting(Setting setting) {
        if (setting == null) throw new IllegalArgumentException("Setting cannot be null!");
        if (!setting.getName().equals(SettingsFactory.DISTANCE_UNITS))
            throw new IllegalArgumentException(SettingsFactory.DISTANCE_UNITS + " setting must be provided!");
        //
        if (setting.getValue().equals(SettingsFactory.MILES)) return MILES;
        else return KMS;
    }
}
