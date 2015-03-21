package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.presenter.MapFragmentPM;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;

/**
 * Created by Edward on 26.01.15.
 * fragment with map and trips
 */
@Layout(R.layout.fragment_map)
@MenuResource(R.menu.menu_map)
public class MapFragment extends BaseFragment<MapFragmentPM> implements MapFragmentPM.View, SearchView.OnQueryTextListener {

    @InjectView(R.id.map)
    ToucheableMapView mapView;
    @InjectView(R.id.container_no_google)
    FrameLayout frameLayoutNoGoogle;

    GoogleMap googleMap;
    private LatLng lastClickedLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        MapsInitializer.initialize(getActivity());
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) != ConnectionResult.SUCCESS) {
            mapView.setVisibility(View.GONE);
            frameLayoutNoGoogle.setVisibility(View.VISIBLE);
        } else {
            mapView.onCreate(savedInstanceState);
        }
        initMap();
        return v;
    }

    private void initMap() {
        mapView.getMapAsync((googleMap) -> {
            this.googleMap = googleMap;
            getPresenter().onMapLoaded();
            this.googleMap.setOnMarkerClickListener((marker) -> {
                getPresenter().onMarkerClick(marker.getSnippet());
                lastClickedLocation = marker.getPosition();
                return true;
            });

            this.mapView.setMapTouchListener(() -> getPresenter().onCameraChanged());
            this.googleMap.setMyLocationEnabled(true);
        });
    }


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnCloseListener(() -> {
            getPresenter().applySearch(null);
            return false;
        });
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getPresenter().onCameraChanged();
        switch (item.getItemId()) {
            case R.id.action_filter:
                ((MainActivity) getActivity()).openRightDrawer();
                break;
            case R.id.action_list:
                getPresenter().actionList();
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
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().onPause();
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
    protected MapFragmentPM createPresenter(Bundle savedInstanceState) {
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
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        getPresenter().applySearch(s);
        return false;
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
                getPresenter().markerReady();
            }

            @Override
            public void onCancel() {

            }
        });
    }
}