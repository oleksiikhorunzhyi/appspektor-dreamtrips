package com.messenger.ui.adapter.inflater;

import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class LiteMapInflater extends ViewInflater {

    private static final int ZOOM_LEVEL = 15;

    @InjectView(R.id.lite_map_view)
    MapView mapView;

    private GoogleMap map;

    private LatLng location;

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

    private void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        MapsInitializer.initialize(context);
        updateMap();
    }


    private void updateMap() {
        if (location != null && map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL));
            map.addMarker(new MarkerOptions().position(location));
            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public void clear() {
        if (map != null) {
            map.clear();
            map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }
}
