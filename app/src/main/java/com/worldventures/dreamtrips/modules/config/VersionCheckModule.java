package com.worldventures.dreamtrips.modules.config;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.config.service.command.LoadConfigurationCommand;
import com.worldventures.dreamtrips.modules.config.service.storage.UpdateRequirementStorage;
import com.worldventures.dreamtrips.modules.config.util.VersionComparator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (complete = false, library = true, injects = {
      LoadConfigurationCommand.class
})
public class VersionCheckModule {

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
