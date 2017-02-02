package com.worldventures.dreamtrips.core.location;

import android.app.Activity;
import android.location.LocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class LocationServiceModule {

   @Singleton
   @Provides
   LocationServiceDispatcher provideLocationServiceDispatcher(LocationManager locationManager, Activity activity) {
      return new LocationServiceDispatcher(locationManager, activity);
   }
}
