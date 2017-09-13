package com.worldventures.dreamtrips.wallet.service.lostcard;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.wallet.di.external.WalletTrackingStatusStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.beacon.BeaconClient;
import com.worldventures.dreamtrips.wallet.service.beacon.WalletBeaconClient;
import com.worldventures.dreamtrips.wallet.service.location.AndroidDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.location.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.WalletLocationCommand;

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
   LostCardRepository locationRepository(WalletStorage walletStorage, WalletTrackingStatusStorage trackingStatusStorage) {
      return new DiskLostCardRepository(walletStorage, trackingStatusStorage);
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
   BeaconClient walletBeaconClient(@ForApplication Context appContext) {
      return new WalletBeaconClient(appContext);
   }

   @Singleton
   @Provides
   LostCardManager locationManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         LocationSyncManager jobScheduler, WalletNetworkService networkService, BeaconClient beaconClient) {
      return new LostCardManager(smartCardInteractor, locationInteractor, jobScheduler, networkService, beaconClient);
   }

   @Singleton
   @Provides
   LocationTrackingManager trackingManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         LogoutInteractor logoutInteractor, WalletDetectLocationService locationService, LostCardManager lostCardManager,
         LoginInteractor loginInteractor, SessionHolder sessionHolder) {
      return new LocationTrackingManager(smartCardInteractor, locationInteractor, logoutInteractor,
            locationService, lostCardManager, loginInteractor, sessionHolder);
   }
}
