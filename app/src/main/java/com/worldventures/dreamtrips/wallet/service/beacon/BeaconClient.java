package com.worldventures.dreamtrips.wallet.service.beacon;

import rx.Observable;

public interface BeaconClient {

   void startScan(RegionBundle bundle);

   void stopScan();

   Observable<BeaconEvent> observeEvents();
}
