package com.messenger.ui.adapter.inflater;

import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class LiteMapInflater extends ViewInflater {

    public static final int ZOOM_LEVEL = 15;

    @InjectView(R.id.lite_map_view)
    MapView mapView;

    private GoogleMap map;

    private LatLng location;

    private GoogleMap.OnMapClickListener onMapClickListener;

    @Override
    public void setView(View rootView) {
        super.setView(rootView);
        mapView.onCreate(null);
        mapView.getMapAsync(this::onMapReady);
    }

    public void setLocation(double latitude, double longitude) {
        this.location = new LatLng(latitude, longitude);
        updateMap();
    }

    public void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        this.onMapClickListener = onMapClickListener;
    }

    private void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        initMap();
        updateMap();
    }

    private void initMap() {
        MapsInitializer.initialize(context);
        map.setOnMapClickListener(this::onMapClick);

        // always only map, disable all additional buttons
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
    }

    private void updateMap() {
        if (location != null && map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL));
            map.addMarker(new MarkerOptions().position(location));
            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void onMapClick(LatLng latLng) {
        if (onMapClickListener != null) {
            onMapClickListener.onMapClick(latLng);
        }
    }

    public void clear() {
        if (map != null) {
            // Clear the map and free up resources by changing the map type to none
            map.clear();
            map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }
}
