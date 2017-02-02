package com.worldventures.dreamtrips.wallet.service.lostcard;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.CreateLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
      CreateLocationCommand.class,
      GetLocationCommand.class},
      library = true, complete = false)
public class LostCardModule {

   @Singleton
   @Provides
   SCLocationRepository locationRepository(@ForApplication SnappyRepository snappyRepository) {
      return new DiskLocationRepository(snappyRepository);
   }

   @Singleton
   @Provides
   SCLocationFacade smartCardlocationFacade(SCLocationRepository locationRepository, SmartCardInteractor smartCardInteractor) {
      return new SCLocationFacade(smartCardInteractor, locationRepository);
   }
}
