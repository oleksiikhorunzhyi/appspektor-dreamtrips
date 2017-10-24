package com.techery.spares.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.techery.spares.storage.complex_objects.ComplexStorageBuilder;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class StorageModule {

   @Provides
   @Singleton
   SharedPreferences provideSharedPreferences(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
   }

   @Provides
   @Singleton
   SimpleKeyValueStorage provideSimpleKeyValueStorage(SharedPreferences preferences) {
      return new SimpleKeyValueStorage(preferences);
   }

   @Provides
   ComplexStorageBuilder provideComplexStorageBuilder(SimpleKeyValueStorage simpleKeyValueStorage) {
      return new ComplexStorageBuilder(simpleKeyValueStorage);
   }

}
