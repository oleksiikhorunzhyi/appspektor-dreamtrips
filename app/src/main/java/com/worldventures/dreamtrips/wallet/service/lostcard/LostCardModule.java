package com.worldventures.dreamtrips.wallet.service.lostcard;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.SaveEnabledTrackingCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            PostLocationCommand.class,
            GetLocationCommand.class,
            DetectGeoLocationCommand.class,
            FetchAddressCommand.class,
            SaveEnabledTrackingCommand.class,
            GetEnabledTrackingCommand.class
      },
      library = true, complete = false)
public class LostCardModule {

   @Singleton
   @Provides
   SCLocationRepository locationRepository(SnappyRepository snappyRepository) {
      return new DiskLocationRepository(snappyRepository);
   }

   @Singleton
   @Provides
   WalletDetectLocationService detectLocationService(@ForApplication Context appContext) {
      return new AndroidDetectLocationService(appContext);
   }

   @Singleton
   @Provides
   LocationSyncManager jobScheduler(SmartCardLocationInteractor locationInteractor, WalletNetworkService networkService) {
      return new LocationSyncManager(locationInteractor, networkService);
   }

   @Singleton
   @Provides
   LostCardManager locationManager(SmartCardLocationInteractor locationInteractor, LocationSyncManager jobScheduler) {
      return new LostCardManager(locationInteractor, jobScheduler);
   }
}
