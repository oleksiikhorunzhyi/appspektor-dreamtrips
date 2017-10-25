package com.worldventures.core.modules.settings;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.modules.settings.command.SettingsCommand;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.modules.settings.storage.SettingsStorageImpl;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            SettingsCommand.class,
      },
      complete = false,
      library = true)
public class SettingsModule {

   @Provides
   @Singleton
   SettingsStorage provideSettingsStorage(@ForApplication Context appContext, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new SettingsStorageImpl(appContext, defaultSnappyOpenHelper);
   }
}
