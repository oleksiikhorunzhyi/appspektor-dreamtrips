package com.worldventures.dreamtrips.social.di;

import com.techery.spares.application.AppInitializer;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
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
