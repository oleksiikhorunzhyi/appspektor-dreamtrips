package com.worldventures.dreamtrips.modules.trips.view.fragment;

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
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
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
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapPresenter;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;

import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

@Layout(R.layout.fragment_trip_map)
@MenuResource(R.menu.menu_map)
public class TripMapFragment extends BaseFragment<TripMapPresenter> implements TripMapPresenter.View {

    protected ToucheableMapView mapView;
    @InjectView(R.id.container_info)
    protected FrameLayout infoContainer;
    @InjectView(R.id.container_no_google)
    protected FrameLayout noGoogleContainer;

    protected GoogleMap googleMap;
    private Bundle mapBundle;
    private static final String KEY_MAP = "map";
    @Icicle LatLng selectedLocation;
    @Icicle boolean searchOpened;


    @Override
    protected TripMapPresenter createPresenter(Bundle savedInstanceState) {
        return new TripMapPresenter();
    }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchOpened) searchItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchOpened = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchOpened = false;
                return true;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQuery(getPresenter().getQuery(), false);
        searchView.setOnCloseListener(() -> {
            TripMapFragment.this.getPresenter().applySearch(null);
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

    ///////////////////////////////////////////////////////////////////////////
    // Map stuff
    ///////////////////////////////////////////////////////////////////////////

    private void initMap() {
        mapView.getMapAsync(map -> {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(marker -> {
                selectedLocation = marker.getPosition();
                getPresenter().onMarkerClick(marker.getSnippet());
                return true;
            });
            mapView.setMapTouchListener(() -> getPresenter().onCameraChanged());
            getPresenter().onMapLoaded();
        });
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)));
    }

    @Override
    public void prepareInfoWindow(int offset) {
        animateToMarker(selectedLocation, offset);
    }

    private void animateToMarker(LatLng latLng, int offset) {
        Projection projection = googleMap.getProjection();
        Point screenLocation = projection.toScreenLocation(latLng);
        screenLocation.y -= offset;
        LatLng offsetTarget = projection.fromScreenLocation(screenLocation);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget), new CancelableCallback() {
            @Override
            public void onFinish() {
                getPresenter().onMarkerInfoPositioned();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (getPresenter() != null) {
                getPresenter().applySearch(s);
            }
            return true;
        }
    };

}