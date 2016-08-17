package com.techery.spares.storage.preferences;

import android.content.SharedPreferences;

public class SimpleKeyValueStorage {
   protected final SharedPreferences appSharedPrefs;

   public SimpleKeyValueStorage(final SharedPreferences preferences) {
      this.appSharedPrefs = preferences;
   }

   public String get(final String key) {
      return this.appSharedPrefs.getString(key, null);
   }

   public void put(final String key, String value) {
      SharedPreferences.Editor prefsEditor = this.appSharedPrefs.edit();
      prefsEditor.putString(key, value);
      prefsEditor.commit();
   }

   public void remove(final String key) {
      SharedPreferences.Editor prefsEditor = this.appSharedPrefs.edit();
      prefsEditor.remove(key);
      prefsEditor.commit();
   }
}

