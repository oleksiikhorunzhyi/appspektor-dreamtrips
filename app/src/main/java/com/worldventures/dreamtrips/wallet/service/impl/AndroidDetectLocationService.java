package com.worldventures.dreamtrips.wallet.service.impl;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;

import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;

public class AndroidDetectLocationService implements WalletDetectLocationService {

   private final Context context;
   private final ReactiveLocationProvider locationProvider;
   private final LocationManager locationManager;

   public AndroidDetectLocationService(Context context) {
      this.context = context;
      this.locationProvider = new ReactiveLocationProvider(context);
      this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
   }

   @Override
   public boolean isPermissionGranted() {
      return PermissionChecker.checkSelfPermission(context, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED;
   }

   @Override
   public boolean isEnabled() {
      return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
   }

   @Override
   public Observable<Boolean> observeLocationSettingState() {
      final RxLocationAdapter adapter = new RxLocationAdapter(context);
      return Observable.create(adapter)
            .doOnUnsubscribe(adapter::release);
   }

   @Override
   public Observable<Location> detectLastKnownLocation() {
      return locationProvider.getLastKnownLocation();
   }

   @Override
   public Observable<Address> obtainAddressByGeoposition(double latitude, double longitude) {
      return locationProvider.getReverseGeocodeObservable(latitude, longitude, 1).map(addresses -> addresses.get(0));
   }

   private static class RxLocationAdapter implements Observable.OnSubscribe<Boolean> {
      private final Context appContext;
      private BroadcastReceiver broadcastReceiver;

      public RxLocationAdapter(Context appContext) {
         this.appContext = appContext;
      }

      @Override
      public void call(Subscriber<? super Boolean> subscriber) {
         broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               final int state = intent.getIntExtra(LocationManager.MODE_CHANGED_ACTION, Settings.Secure.LOCATION_MODE_OFF);
               subscriber.onNext(Settings.Secure.LOCATION_MODE_OFF != state);
            }
         };
         appContext.registerReceiver(broadcastReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
      }

      public void release() {
         appContext.unregisterReceiver(broadcastReceiver);
      }
   }
}
