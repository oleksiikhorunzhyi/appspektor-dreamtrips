package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripMapListBundle;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import icepick.Icepick;
import icepick.State;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@Layout(R.layout.fragment_trips_map)
@MenuResource(R.menu.menu_map)
public class TripMapFragment extends RxBaseFragment<TripMapPresenter> implements TripMapPresenter.View {

    private static final String KEY_MAP = "map";

    protected ToucheableMapView mapView;
    @InjectView(R.id.container_no_google)
    protected FrameLayout noGoogleContainer;

    protected GoogleMap googleMap;
    private Bundle mapBundle;

    @Inject
    BackStackDelegate backStackDelegate;
    @State
    LatLng selectedLocation;
    @State
    boolean searchOpened;
    private Subscription mapChangesSubscription;

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
    public void onResume() {
        super.onResume();
        mapView.onResume();
        getActivity().setTitle(R.string.trips);
        backStackDelegate.setListener(this::onBackPressed);
    }

    private boolean onBackPressed() {
        if (getChildFragmentManager().findFragmentById(R.id.container_info) instanceof TripMapListFragment) {
            router.moveTo(Route.MAP_INFO, NavigationConfigBuilder.forRemoval()
                    .containerId(R.id.container_info)
                    .fragmentManager(getChildFragmentManager())
                    .build());
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        backStackDelegate.setListener(null);
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
        if (mapChangesSubscription != null && !mapChangesSubscription.isUnsubscribed()) {
            mapChangesSubscription.unsubscribe();
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
            mapView.setMapTouchListener(this::onMapTouched);
            onMapLoaded();
        });
    }

    @Override
    protected void onMenuInflated(Menu menu) {
        super.onMenuInflated(menu);
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
                router.back();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Map stuff
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Marker addPin(Bitmap pinBitmap, MapObject mapObject) {
        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(mapObject.getCoordinates().getLat(), mapObject.getCoordinates().getLng()))
                .icon(BitmapDescriptorFactory.fromBitmap(pinBitmap)));
    }

    @Override
    public void moveTo(List<TripModel> trips) {
        router.moveTo(Route.MAP_INFO, NavigationConfigBuilder.forFragment()
                .containerId(R.id.container_info)
                .fragmentManager(getChildFragmentManager())
                .backStackEnabled(false)
                .data(new TripMapListBundle(trips))
                .build());
    }

    @Override
    public void removeTripsPopupInfo() {
        if (getChildFragmentManager().findFragmentById(R.id.container_info) instanceof TripMapListFragment)
            router.moveTo(Route.MAP_INFO, NavigationConfigBuilder.forRemoval()
                    .containerId(R.id.container_info)
                    .fragmentManager(getChildFragmentManager())
                    .build());
    }

    @Override
    public void zoomToBounds(LatLngBounds latLngBounds) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
    }

    @Override
    public GoogleMap getMap() {
        return googleMap;
    }

    protected void onMapLoaded() {
        getPresenter().onMapLoaded();
        mapChangesSubscription = subscribeToCameraChanges();
    }

    private Subscription subscribeToCameraChanges() {
        return MapObservableFactory.createCameraChangeObservable(googleMap)
                .throttleLast(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cameraPosition -> {
                    getPresenter().reloadMapObjects();
                }, error -> {
                    Timber.e(error.getMessage());
                });
    }


    protected void onMapTouched() {
        getPresenter().onCameraChanged();
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

