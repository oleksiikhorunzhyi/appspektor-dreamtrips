package com.worldventures.dreamtrips.core.preference;

import android.content.SharedPreferences;

import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

public class Prefs extends SimpleKeyValueStorage {

    public static final String LAST_SYNC = "LAST_SYNC";

    public Prefs(SharedPreferences preferences) {
        super(preferences);
    }

    public long getLong(final String key) {
        return this.appSharedPrefs.getLong(key, 0L);
    }

    public boolean getBoolean(final String key) {
        return this.appSharedPrefs.getBoolean(key, false);
    }

    public void put(final String key, Long value) {
        SharedPreferences.Editor prefsEditor = this.appSharedPrefs.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.apply();
    }

    public void put(final String key, Boolean value) {
        SharedPreferences.Editor prefsEditor = this.appSharedPrefs.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

}