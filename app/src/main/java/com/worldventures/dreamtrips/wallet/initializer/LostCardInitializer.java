package com.worldventures.dreamtrips.wallet.initializer;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.beacon.WalletBeaconClient;
import com.worldventures.dreamtrips.wallet.service.lostcard.LocationTrackingManager;

import javax.inject.Inject;

import pl.brightinventions.slf4android.LoggerConfiguration;

public class LostCardInitializer implements AppInitializer {

   @Inject LocationTrackingManager trackingManager;

   private final Context context;

   public LostCardInitializer(Context context) {
      this.context = context;
   }

   @Override
   public void initialize(Injector injector) {
      // TODO: 7/31/17 REMOVE after finalizing Beacons feature
      LoggerConfiguration.configuration()
            .addHandlerToLogger(WalletBeaconClient.TAG, LoggerConfiguration.fileLogHandler(context));
      injector.inject(this);
      trackingManager.track();
   }
}
