package com.worldventures.dreamtrips.social.di;

import com.techery.spares.application.AppInitializer;
import com.worldventures.dreamtrips.core.initializer.CachedEntityCommandInitializer;
import com.worldventures.dreamtrips.core.initializer.VersionCheckInitializer;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SocialInitializerModule {

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideCachedEntitiesInitializer(CachedEntityInteractor interactor) {
      return new CachedEntityCommandInitializer(interactor);
   }

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideVersionCheckInitializer(AppConfigurationInteractor interactor) {
      return new VersionCheckInitializer(interactor);
   }


}
