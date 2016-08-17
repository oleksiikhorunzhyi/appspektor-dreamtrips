package com.worldventures.dreamtrips.modules.map.view;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;

import butterknife.InjectView;
import icepick.Icepick;

public abstract class MapFragment<T extends Presenter> extends RxBaseFragment<T> {
   private static final String KEY_MAP = "map";

   protected Integer cameraAnimationDuration = 1000;

   protected ToucheableMapView mapView;
   @InjectView(R.id.container_info) protected FrameLayout infoContainer;
   @InjectView(R.id.container_no_google) protected FrameLayout noGoogleContainer;

   protected GoogleMap googleMap;
   private Bundle mapBundle;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Icepick.restoreInstanceState(this, savedInstanceState);
      if (savedInstanceState != null) mapBundle = savedInstanceState.getBundle(KEY_MAP);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
      if (mapView != null) {
         Bundle mapBundle = new Bundle();
         outState.putBundle(KEY_MAP, mapBundle);
         mapView.onSaveInstanceState(mapBundle);
      }
      super.onSaveInstanceState(outState);
   }

   @Override
   public void afterCreateView(View rootView) {
      mapView = (ToucheableMapView) rootView.findViewById(R.id.map);
      if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) != ConnectionResult.SUCCESS) {
         mapView.setVisibility(View.GONE);
         noGoogleContainer.setVisibility(View.VISIBLE);
      } else {
         MapsInitializer.initialize(rootView.getContext());
         mapView.onCreate(mapBundle);
      }
      initMap();
   }

   @Override
   public void onResume() {
      super.onResume();
      mapView.onResume();
   }

   @Override
   public void onPause() {
      super.onPause();
      mapView.onPause();
   }

   @Override
   public void onDestroyView() {
      if (mapView != null) {
         mapView.removeAllViews();
      }
      if (googleMap != null) {
         googleMap.clear();
         googleMap.setOnMarkerClickListener(null);
      }
      super.onDestroyView();
   }


   @Override
   public void onDestroy() {
      super.onDestroy();
      if (mapView != null) {
         mapView.onDestroy();
         mapView = null;
      }
      googleMap = null;
   }

   @Override
   public void onLowMemory() {
      super.onLowMemory();
      mapView.onLowMemory();
   }

   private void initMap() {
      mapView.getMapAsync(map -> {
         googleMap = map;
         googleMap.setMyLocationEnabled(true);
         googleMap.setOnMarkerClickListener(this::onMarkerClick);
         mapView.setMapTouchListener(this::onMapTouched);
         onMapLoaded();
      });
   }

   protected void animateToMarker(LatLng latLng, int offset) {
      Projection projection = googleMap.getProjection();
      Point screenLocation = projection.toScreenLocation(latLng);
      screenLocation.y -= offset;
      LatLng offsetTarget = projection.fromScreenLocation(screenLocation);
      googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget), cameraAnimationDuration, new GoogleMap.CancelableCallback() {
         @Override
         public void onFinish() {
            onMarkerFocused();
         }

         @Override
         public void onCancel() {
         }
      });
   }

   protected abstract boolean onMarkerClick(Marker marker);

   protected abstract void onMapLoaded();

   protected abstract void onMarkerFocused();

   protected abstract void onMapTouched();
}
