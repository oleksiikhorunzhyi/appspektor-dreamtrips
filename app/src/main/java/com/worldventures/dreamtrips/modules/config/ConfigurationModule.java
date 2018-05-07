package com.worldventures.dreamtrips.modules.config;

import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.config.service.storage.UpdateRequirementStorage;
import com.worldventures.dreamtrips.modules.config.util.VersionComparator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ConfigurationModule {

   @Provides
   @Singleton
   VersionComparator provideVersionComparator() {
      return new VersionComparator();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideStorage(SnappyRepository snappyRepository) {
      return new UpdateRequirementStorage(snappyRepository);
   }
}
