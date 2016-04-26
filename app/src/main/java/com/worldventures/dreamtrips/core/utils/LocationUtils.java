package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import timber.log.Timber;

public class LocationUtils {

    public static boolean isGpsOn(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                Timber.e(e, "");
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !android.text.TextUtils.isEmpty(locationProviders);
        }
    }
}
