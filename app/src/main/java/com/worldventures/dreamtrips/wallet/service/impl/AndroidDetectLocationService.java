package com.worldventures.dreamtrips.wallet.service.impl;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.PermissionChecker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.util.LocationUnavailableException;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static rx.Observable.create;
import static rx.Observable.fromCallable;

public class AndroidDetectLocationService implements WalletDetectLocationService {

   private final Context context;
   private final LocationManager locationManager;

   public AndroidDetectLocationService(Context context) {
      this.context = context;
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
            .map(aVoid -> isEnabled())
            .distinctUntilChanged()
            .doOnUnsubscribe(adapter::release);
   }

   @Override
   public Observable<Location> detectLastKnownLocation() {
      return connectObservable().flatMap(googleApiClient ->
            fromCallable(() -> FusedLocationApi.getLastLocation(googleApiClient))
                  .filter(location -> location != null)
                  .switchIfEmpty(requestUpdatedLocation(googleApiClient).take(1))
      );
   }

   private Observable<Location> requestUpdatedLocation(GoogleApiClient googleApiClient) {
      final LocationSettingsResult locationSettingsResult = checkSettings(googleApiClient);
      final Status status = locationSettingsResult.getStatus();
      if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
         return Observable.error(new LocationUnavailableException(locationSettingsResult));
      }

      return Observable.create(new RxFusedLocationApi(googleApiClient, provideLocationRequest()));
   }

   private LocationSettingsResult checkSettings(GoogleApiClient googleApiClient) {
      return LocationServices.SettingsApi.checkLocationSettings(googleApiClient, new LocationSettingsRequest.Builder()
            .addLocationRequest(provideLocationRequest())
            .setAlwaysShow(true)
            .build())
            .await();
   }

   private LocationRequest provideLocationRequest() {
      return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setNumUpdates(1)
            .setInterval(1000);
   }

   private static class RxLocationAdapter implements Observable.OnSubscribe<Void> {
      private final Context appContext;
      private BroadcastReceiver broadcastReceiver;

      RxLocationAdapter(Context appContext) {
         this.appContext = appContext;
      }

      @Override
      public void call(Subscriber<? super Void> subscriber) {
         broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               subscriber.onNext(null);
            }
         };
         appContext.registerReceiver(broadcastReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
      }

      public void release() {
         appContext.unregisterReceiver(broadcastReceiver);
      }
   }

   private static class RxFusedLocationApi implements Observable.OnSubscribe<Location> {

      private final GoogleApiClient googleApiClient;
      private final LocationRequest locationRequest;

      private RxFusedLocationApi(GoogleApiClient googleApiClient, LocationRequest locationRequest) {
         this.googleApiClient = googleApiClient;
         this.locationRequest = locationRequest;
      }

      @Override
      public void call(Subscriber<? super Location> subscriber) {
         LocationListener listener = subscriber::onNext;
         FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener); // TODO: 7/20/17 use looper
         subscriber.add(Subscriptions.create(() -> FusedLocationApi.removeLocationUpdates(googleApiClient, listener)));
      }
   }

   private Observable<GoogleApiClient> connectObservable() {
      return create(subscriber -> {
         final ClientWithStatus clientWithStatus = createAndConnect();
         if (clientWithStatus.connectionResult.isSuccess()) {
            subscriber.onNext(clientWithStatus.googleApiClient);
         }
         subscriber.onCompleted();
         subscriber.add(Subscriptions.create(() -> {
            clientWithStatus.googleApiClient.disconnect();
            Timber.d("connectObservable googleApiClient.disconnect Tread %s", Thread.currentThread());
         }));
      });
   }

   private ClientWithStatus createAndConnect() {
      final GoogleApiClient client = createLocationApiClient();
      return new ClientWithStatus(client, client.blockingConnect());
   }

   private GoogleApiClient createLocationApiClient() {
      return new GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .build();
   }

   private static class ClientWithStatus {
      final GoogleApiClient googleApiClient;
      final ConnectionResult connectionResult;

      private ClientWithStatus(GoogleApiClient googleApiClient, ConnectionResult connectionResult) {
         this.googleApiClient = googleApiClient;
         this.connectionResult = connectionResult;
      }
   }
}
