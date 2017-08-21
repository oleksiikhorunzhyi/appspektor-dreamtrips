package com.worldventures.dreamtrips.wallet.service.lostcard;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.di.external.WalletTrackingStatusStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.location.AndroidDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.location.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.WalletLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.UpdateTrackingStatusCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            PostLocationCommand.class,
            GetLocationCommand.class,
            DetectGeoLocationCommand.class,
            FetchAddressWithPlacesCommand.class,
            FetchTrackingStatusCommand.class,
            WalletLocationCommand.class,
            UpdateTrackingStatusCommand.class
      },
      library = true, complete = false)
public class LostCardModule {

   @Singleton
   @Provides
   LostCardRepository locationRepository(SnappyRepository snappyRepository, WalletTrackingStatusStorage trackingStatusStorage) {
      return new DiskLostCardRepository(snappyRepository, trackingStatusStorage);
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
   LostCardManager locationManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         LocationSyncManager jobScheduler, WalletNetworkService networkService) {
      return new LostCardManager(smartCardInteractor, locationInteractor, jobScheduler, networkService);
   }

   @Singleton
   @Provides
   LocationTrackingManager trackingManager(SmartCardLocationInteractor locationInteractor,
         WalletDetectLocationService locationService,
         LostCardManager lostCardManager) {
      return new LocationTrackingManager(locationInteractor, locationService, lostCardManager);
   }
}
