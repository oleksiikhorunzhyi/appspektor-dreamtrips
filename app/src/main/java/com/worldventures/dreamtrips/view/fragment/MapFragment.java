package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.presentation.MapFragmentPM;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.custom.ToucheableMapView;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by Edward on 26.01.15.
 * fragment with map and trips
 */
@Layout(R.layout.fragment_map)
@MenuResource(R.menu.menu_map)
public class MapFragment extends BaseFragment<MapFragmentPM> implements MapFragmentPM.View {

    @InjectView(R.id.map)
    ToucheableMapView mapView;

    GoogleMap googleMap;

    private LatLng lastClickedLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        MapsInitializer.initialize(getActivity());
        mapView.onCreate(savedInstanceState);
        initMap();
        return v;
    }

    private void initMap() {
        mapView.getMapAsync((googleMap) -> {
            this.googleMap = googleMap;
            getPresentationModel().onMapLoaded();
            this.googleMap.setOnMarkerClickListener((marker) -> {
                getPresentationModel().onMarkerClick(marker.getSnippet());
                lastClickedLocation = marker.getPosition();
                return true;
            });

            this.mapView.setMapTouchListener(() -> getPresentationModel().onCameraChanged());
            this.googleMap.setMyLocationEnabled(true);
        });
    }


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                ((MainActivity) getActivity()).openRightDrawer();
                break;
            case R.id.action_list:
                getPresentationModel().actionList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mapView != null)
            mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected MapFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new MapFragmentPM(this);
    }

    @Override
    public void clearMap() {
        googleMap.clear();
    }

    @Override
    public void addPin(LatLng latLng, int id) {
        googleMap.addMarker(new MarkerOptions()
                .snippet(String.valueOf(id))
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));
    }

    private void zoomIn() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomIn();
        googleMap.moveCamera(cameraUpdate);
    }


    @Override
    public void showInfoWindow(int offset) {
        updateMap(lastClickedLocation, offset);
    }

    private void updateMap(final LatLng latLng, int offset) {
        Projection projection = googleMap.getProjection();
        Point screenLocation = projection.toScreenLocation(latLng);
        screenLocation.y -= offset;
        LatLng offsetTarget = projection.fromScreenLocation(screenLocation);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                getPresentationModel().markerReady();
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
