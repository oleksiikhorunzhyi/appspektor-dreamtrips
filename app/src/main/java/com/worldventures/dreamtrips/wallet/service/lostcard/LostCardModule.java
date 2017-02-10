package com.worldventures.dreamtrips.wallet.service.lostcard;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.FetchPlacesNearbyCommand;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.SaveEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.WalletLocationCommand;

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
            GetEnabledTrackingCommand.class,
            FetchPlacesNearbyCommand.class,
            WalletLocationCommand.class
      },
      library = true, complete = false)
public class LostCardModule {

   @Singleton
   @Provides
   LostCardRepository locationRepository(SnappyRepository snappyRepository) {
      return new DiskLostCardRepository(snappyRepository);
   }

   @Singleton
   @Provides
   WalletDetectLocationService detectLocationService(@ForApplication Context appContext) {
      return new AndroidDetectLocationService(appContext);
   }

   @Singleton
   @Provides
   LocationSyncManager jobScheduler(SmartCardLocationInteractor locationInteractor) {
      return new LocationSyncManager(locationInteractor);
   }

   @Singleton
   @Provides
   LostCardManager locationManager(SmartCardLocationInteractor locationInteractor, LocationSyncManager jobScheduler,
         WalletNetworkService networkService) {
      return new LostCardManager(locationInteractor, jobScheduler, networkService);
   }

   @Singleton
   @Provides
   LocationTrackingManager trackingManager(SmartCardLocationInteractor locationInteractor, LostCardManager lostCardManager) {
      return new LocationTrackingManager(locationInteractor, lostCardManager);
   }
}
