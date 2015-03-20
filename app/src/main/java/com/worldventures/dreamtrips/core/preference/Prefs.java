package com.worldventures.dreamtrips.core.preference;

import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;

import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

import java.util.Map;

public class Prefs extends SimpleKeyValueStorage {

    public static final String LAST_SYNC = "LAST_SYNC";
    public static final String LAST_SYNC_BUCKET = "LAST_SYNC_BUCKET";
    public static final String PREFIX = "bucket_";

    public Prefs(SharedPreferences preferences) {
        super(preferences);
    }

    public long getLong(final String key) {
        return this.appSharedPrefs.getLong(key, 0l);
    }

    public void clear() {
        Map<String, String> preserve = new ArrayMap<>();
        Map<String, ?> all = appSharedPrefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith(PREFIX)) {
                preserve.put(entry.getKey(), entry.getValue().toString());
            }
        }

        SharedPreferences.Editor edit = this.appSharedPrefs.edit();
        edit.clear().apply();

        for (Map.Entry<String, String> entry : preserve.entrySet()) {
            edit.putString(entry.getKey(), entry.getValue());
        }
        edit.apply();

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