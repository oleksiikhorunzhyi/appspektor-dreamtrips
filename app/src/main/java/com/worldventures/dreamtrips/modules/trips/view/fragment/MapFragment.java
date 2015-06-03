package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.trips.presenter.MapPresenter;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;

import butterknife.InjectView;

@Layout(R.layout.fragment_map)
@MenuResource(R.menu.menu_map)
public class MapFragment extends BaseFragment<MapPresenter> implements MapPresenter.View {

    @InjectView(R.id.map)
    protected ToucheableMapView mapView;
    @InjectView(R.id.container_no_google)
    protected FrameLayout frameLayoutNoGoogle;

    protected GoogleMap googleMap;
    private LatLng lastClickedLocation;

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (getPresenter() != null) {
                getPresenter().applySearch(s);
                getPresenter().onCameraChanged();
            }
            return false;
        }
    };

    private GoogleMap.CancelableCallback cancelableCallback = new GoogleMap.CancelableCallback() {
        @Override
        public void onFinish() {
            getPresenter().markerReady();
        }

        @Override
        public void onCancel() {
            //nothing to do here
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsInitializer.initialize(getActivity());
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) != ConnectionResult.SUCCESS) {
            mapView.setVisibility(View.GONE);
            frameLayoutNoGoogle.setVisibility(View.VISIBLE);
        } else {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void afterCreateView(View rootView) {
        initMap();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getPresenter().onCameraChanged();
    }

    private void initMap() {
        mapView.getMapAsync((map) -> {
            this.googleMap = map;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnCloseListener(() -> {
            getPresenter().applySearch(null);
            return false;
        });
        searchView.setOnQueryTextListener(onQueryTextListener);
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
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        cancelableCallback = null;
        onQueryTextListener = null;
        mapView.removeAllViews();
        mapView.onDestroy();
        mapView = null;
        googleMap = null;
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Override
    protected MapPresenter createPresenter(Bundle savedInstanceState) {
        return new MapPresenter();
    }

    @Override
    public void clearMap() {
        if (googleMap != null) {
            googleMap.clear();
        }
    }

    @Override
    public void addPin(LatLng latLng, String id) {
        googleMap.addMarker(new MarkerOptions()
                .snippet(String.valueOf(id))
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget), cancelableCallback);
    }
}