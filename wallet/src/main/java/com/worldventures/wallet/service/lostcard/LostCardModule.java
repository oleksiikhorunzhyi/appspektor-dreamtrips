package com.worldventures.wallet.service.lostcard;

import android.content.Context;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.wallet.domain.WalletTrackingStatusStorage;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.WalletNetworkService;
import com.worldventures.wallet.service.beacon.BeaconClient;
import com.worldventures.wallet.service.beacon.BeaconLoggerModule;
import com.worldventures.wallet.service.beacon.WalletBeaconClient;
import com.worldventures.wallet.service.beacon.WalletBeaconLogger;
import com.worldventures.core.service.location.DetectLocationService;
import com.worldventures.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.wallet.service.lostcard.command.FetchTrackingStatusCommand;
import com.worldventures.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.wallet.service.lostcard.command.WalletLocationCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = BeaconLoggerModule.class,
      injects = {
            PostLocationCommand.class,
            GetLocationCommand.class,
            DetectGeoLocationCommand.class,
            FetchAddressWithPlacesCommand.class,
            FetchTrackingStatusCommand.class,
            WalletLocationCommand.class,
            UpdateTrackingStatusCommand.class,
      },
      library = true, complete = false)
public class LostCardModule {

   @Singleton
   @Provides
   LostCardRepository locationRepository(WalletStorage walletStorage, WalletTrackingStatusStorage trackingStatusStorage) {
      return new DiskLostCardRepository(walletStorage, trackingStatusStorage);
   }

   @Singleton
   @Provides
   LocationSyncManager jobScheduler(SmartCardLocationInteractor locationInteractor) {
      return new LocationSyncManager(locationInteractor);
   }

   @Singleton
   @Provides
   BeaconClient walletBeaconClient(Context appContext, WalletBeaconLogger beaconLogger) {
      return new WalletBeaconClient(appContext, beaconLogger);
   }

   @Singleton
   @Provides
   LostCardManager locationManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         LocationSyncManager jobScheduler, WalletNetworkService networkService, BeaconClient beaconClient, WalletBeaconLogger beaconLogger) {
      return new LostCardManager(smartCardInteractor, locationInteractor, jobScheduler, networkService, beaconClient, beaconLogger);
   }

   @Singleton
   @Provides
   LocationTrackingManager trackingManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         DetectLocationService locationService, AuthInteractor authInteractor, LostCardManager lostCardManager, SessionHolder sessionHolder) {
      return new LocationTrackingManager(smartCardInteractor, locationInteractor, locationService, authInteractor,
            lostCardManager, sessionHolder);
   }
}
