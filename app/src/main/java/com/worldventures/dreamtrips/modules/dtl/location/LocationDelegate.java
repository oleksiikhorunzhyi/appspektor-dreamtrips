package com.worldventures.dreamtrips.modules.dtl.location;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;

import rx.Observable;

public interface LocationDelegate {

   void setPermissionView(PermissionView permissionView);

   void dropPermissionView();

   void tryRequestLocation();

   void attachListener(LocationDelegate.LocationListener locationListener);

   void detachListener(LocationDelegate.LocationListener locationListener);

   void onLocationObtained(@Nullable Location location);

   Observable<Location> getLastKnownLocation();

   Observable<Location> getLastKnownLocationOrEmpty();

   Observable<Location> requestLocationUpdate();

   class LocationException extends Exception {

      Status status;

      public LocationException(Status status) {
         this.status = status;
      }

      public Status getStatus() {
         return status;
      }
   }

   interface LocationListener {
      void onLocationObtained(Location location);
   }
}
