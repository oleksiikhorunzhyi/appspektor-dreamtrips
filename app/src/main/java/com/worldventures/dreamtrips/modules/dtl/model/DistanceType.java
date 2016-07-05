package com.worldventures.dreamtrips.modules.dtl.model;

import com.crashlytics.android.Crashlytics;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

public enum DistanceType {
    MILES("mi"), KMS("km");

    String analyticsTypeName;

    DistanceType(String analyticsTypeName) {
        this.analyticsTypeName = analyticsTypeName;
    }

    public String getTypeNameForAnalytics() {
        return analyticsTypeName;
    }

    public static DistanceType provideFromSetting(Setting setting) {
        if (setting == null) {
            Crashlytics.logException(new NullPointerException("Setting cannot be null!"));
            return MILES; // safety-patch, should never happen
        }
        if (!setting.getName().equals(SettingsFactory.DISTANCE_UNITS))
            throw new IllegalArgumentException(SettingsFactory.DISTANCE_UNITS +
                    " setting must be provided!");
        //
        return setting.getValue().equals(SettingsFactory.MILES) ? MILES : KMS;
    }
}
