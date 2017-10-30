package com.worldventures.wallet.service.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

@SuppressWarnings("FieldCanBeLocal")
public class WalletBeaconClient implements BeaconClient, BeaconConsumer, BootstrapNotifier {

   public static final String TAG = "Beacon client";
   private static final Logger FILE_LOGGER = LoggerFactory.getLogger(TAG);
   private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

   private final PublishSubject<BeaconEvent> beaconEventPipe = PublishSubject.create();

   private final Context context;
   private final BeaconManager beaconManager;

   @SuppressWarnings("unused")
   private BackgroundPowerSaver backgroundPowerSaver;
   @SuppressWarnings("unused")
   private RegionBootstrap regionBootstrap;

   private Region scanRegion;

   public static void logBeacon(String s, Object... args) {
      if (args.length > 0) {
         s = String.format(s, args);
      }
      Timber.d("%s :: %s", TAG, s);
      FILE_LOGGER.debug(s);
   }


   public WalletBeaconClient(Context context) {
      this.context = context;

      beaconManager = BeaconManager.getInstanceForApplication(context);
      beaconManager.setBackgroundMode(true);
      beaconManager.setEnableScheduledScanJobs(true);
      beaconManager.getBeaconParsers().clear();
      beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_LAYOUT));
   }

   @Override
   public Observable<BeaconEvent> observeEvents() {
      return beaconEventPipe.asObservable();
   }

   @Override
   public void startScan(RegionBundle bundle) {
      if (beaconManager.isBound(this)) {
         return;
      }

      try {
         WalletBeaconClient.logBeacon("Start service :: SmartCard ID - %s", bundle.getMajor());
         prepareRegion(bundle);
         beaconManager.bind(this);
      } catch (BeaconManager.ServiceNotDeclaredException e) {
         Timber.e(e, "Beacon client :: startScan");
      }
   }

   private void prepareRegion(RegionBundle bundle) {
      final String minor = bundle.getMinor();
      final String major = bundle.getMajor();
      scanRegion = new Region(bundle.getName(), Identifier.parse(bundle.getUuid()),
            minor == null ? null : Identifier.parse(minor),
            major == null ? null : Identifier.parse(major));
   }

   @Override
   public void onBeaconServiceConnect() {
      WalletBeaconClient.logBeacon("Service connected");
      regionBootstrap = new RegionBootstrap(this, scanRegion);
      backgroundPowerSaver = new BackgroundPowerSaver(context);
   }

   @Override
   public void stopScan() {
      WalletBeaconClient.logBeacon("Stop scan");
      if (regionBootstrap != null) {
         regionBootstrap.disable();
         regionBootstrap = null;
      }
      backgroundPowerSaver = null;
      beaconManager.unbind(this);
   }

   @Override
   public void didEnterRegion(Region region) {
      beaconEventPipe.onNext(new BeaconEvent(region, true));
   }

   @Override
   public void didExitRegion(Region region) {
      beaconEventPipe.onNext(new BeaconEvent(region, false));
   }

   @Override
   public void didDetermineStateForRegion(int i, Region region) {
      //do nothing
   }

   @Override
   public Context getApplicationContext() {
      return context.getApplicationContext();
   }

   @Override
   public boolean bindService(Intent service, ServiceConnection connection, int flags) {
      return context.bindService(service, connection, flags);
   }

   @Override
   public void unbindService(ServiceConnection connection) {
      context.unbindService(connection);
   }
}
