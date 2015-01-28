package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.presentation.MapFragmentPM;
import com.worldventures.dreamtrips.view.activity.MainActivity;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by Edward on 26.01.15.
 * fragment with map and trips
 */
@Layout(R.layout.fragment_map)
@MenuResource(R.menu.menu_map)
public class MapFragment extends BaseFragment<MapFragmentPM> implements MapFragmentPM.View {

    public static final String EXTRA_TRIPS = "EXTRA_TRIPS";

    private static Handler handler = new Handler();

    @InjectView(R.id.map)
    MapView mapView;

    GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        MapsInitializer.initialize(getActivity());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((googleMap) -> {
            this.googleMap = googleMap;
            getPresentationModel().onMapLoaded();
            this.googleMap.setOnMarkerClickListener((marker) -> {
                updateMap(marker.getPosition(), marker.getSnippet());
                return true;
            });
            this.googleMap.setOnCameraChangeListener((cameraPosition) -> {
                getPresentationModel().onCameraChanged();
            });
        });
        return v;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        getPresentationModel().setData((ArrayList<Trip>) getArguments().getSerializable(EXTRA_TRIPS));
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
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
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

    private void updateMap(final LatLng latLng, final String id) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10.0f);
        googleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                addOffset(latLng, id);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void addOffset(final LatLng latLng, final String id) {
        Projection projection = googleMap.getProjection();
        Point screenLocation = projection.toScreenLocation(latLng);
        screenLocation.y -= getResources().getDimensionPixelSize(R.dimen.map_offset_y);
        LatLng offsetTarget = projection.fromScreenLocation(screenLocation);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                handler.postDelayed(()->getPresentationModel().onMarkerClick(id), 20);
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
