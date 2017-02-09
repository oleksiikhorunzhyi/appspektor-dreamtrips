package com.worldventures.dreamtrips.core.location;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class LocationServiceModule {

   @Singleton
   @Provides
   GoogleApiClient provideGoogleApiClient(Context context) {
      return new GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .build();
   }

   @Singleton
   @Provides
   LocationServiceDispatcher provideLocationServiceDispatcher(Activity activity, LocationManager locationManager, GoogleApiClient googleApiClient) {
      return new LocationServiceDispatcher(activity, locationManager, googleApiClient);
   }
}
