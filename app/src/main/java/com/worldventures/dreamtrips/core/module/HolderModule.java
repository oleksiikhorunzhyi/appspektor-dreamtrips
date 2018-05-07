package com.worldventures.dreamtrips.core.module;

import com.worldventures.core.model.AppVersionHolder;
import com.worldventures.core.model.session.FeatureManager;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.storage.preferences.SimpleKeyValueStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class HolderModule {

   @Provides
   @Singleton
   public SessionHolder provideSessionHolder(SimpleKeyValueStorage simpleKeyValueStorage) {
      return new SessionHolder(simpleKeyValueStorage);
   }

   @Provides
   @Singleton
   public AppVersionHolder provideAppVersionHolder(SimpleKeyValueStorage simpleKeyValueStorage) {
      return new AppVersionHolder(simpleKeyValueStorage);
   }

   @Provides
   @Singleton
   public FeatureManager provideFeatureManager(SessionHolder sessionHolder) {
      return new FeatureManager(sessionHolder);
   }
}
