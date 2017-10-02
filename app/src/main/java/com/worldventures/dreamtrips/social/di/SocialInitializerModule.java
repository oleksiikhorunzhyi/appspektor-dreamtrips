package com.worldventures.dreamtrips.social.di;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.dreamtrips.social.initializer.CachedEntityCommandInitializer;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SocialInitializerModule {

   @Provides(type = Provides.Type.SET)
   public AppInitializer provideCachedEntitiesInitializer(CachedEntityInteractor interactor) {
      return new CachedEntityCommandInitializer(interactor);
   }
}
