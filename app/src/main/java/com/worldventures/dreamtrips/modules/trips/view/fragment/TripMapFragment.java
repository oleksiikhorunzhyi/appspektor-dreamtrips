package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.map.view.MapFragment;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapPresenter;

import icepick.State;


@Layout(R.layout.fragment_map_with_info)
@MenuResource(R.menu.menu_map)
public class TripMapFragment extends MapFragment<TripMapPresenter> implements TripMapPresenter.View {

    @State LatLng selectedLocation;
    @State
    boolean searchOpened;

    @Override
    protected TripMapPresenter createPresenter(Bundle savedInstanceState) {
        return new TripMapPresenter();
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

    ///////////////////////////////////////////////////////////////////////////
    // Map stuff
    ///////////////////////////////////////////////////////////////////////////

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

    @Override
    protected boolean onMarkerClick(Marker marker) {
        selectedLocation = marker.getPosition();
        getPresenter().onMarkerClick(marker.getSnippet());
        return true;
    }

    @Override
    protected void onMapLoaded() {
        getPresenter().onMapLoaded();
    }

    @Override
    protected void onMarkerFocused() {
        getPresenter().onMarkerInfoPositioned();
    }

    @Override
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