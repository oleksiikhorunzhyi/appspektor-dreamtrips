package com.worldventures.dreamtrips.modules.picklocation.presenter;

import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.worldventures.dreamtrips.modules.picklocation.view.PickLocationView;

public interface PickLocationPresenter extends MvpPresenter<PickLocationView> {

   void onRationalForLocationPermissionRequired();

   void onLocationPermissionGranted();

   void onLocationPermissionDenied();

   void onShouldInitMap();

   void onMapInitialized(GoogleMap googleMap);

   int getToolbarMenuRes();

   boolean onMenuItemClick(MenuItem item);
}
