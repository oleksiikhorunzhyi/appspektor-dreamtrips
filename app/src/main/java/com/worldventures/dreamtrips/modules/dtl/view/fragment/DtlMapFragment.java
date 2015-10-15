package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapPresenter;
import com.worldventures.dreamtrips.modules.map.view.MapFragment;

import icepick.State;

@Layout(R.layout.fragment_map_with_info)
@MenuResource(R.menu.menu_dtl_map)
public class DtlMapFragment extends MapFragment<DtlMapPresenter> implements DtlMapPresenter.View {

    FragmentCompass infoFragmentCompass;
    //
    @State
    LatLng selectedLocation;

    @Override
    protected DtlMapPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMapPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        infoFragmentCompass = new FragmentCompass((BaseActivity) getActivity(), R.id.container_info);
        infoFragmentCompass.setSupportFragmentManager(getChildFragmentManager());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list:
                fragmentCompass.pop();
                break;
            case R.id.action_dtl_filter:
                ((MainActivity) getActivity()).openRightDrawer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected boolean onMarkerClick(Marker marker) {
        selectedLocation = marker.getPosition();
        getPresenter().onMarkerClick(Integer.valueOf(marker.getSnippet()));
        return true;
    }

    @Override
    protected void onMapLoaded() {
        getPresenter().onMapLoaded();
    }

    @Override
    protected void onMarkerFocused() {
        eventBus.post(new DtlShowMapInfoEvent());
    }

    @Override
    protected void onMapTouched() {
        hideInfoIfShown();
    }

    private void hideInfoIfShown() {
        infoFragmentCompass.remove(Route.DTL_MAP_INFO);
    }

    @Override
    public void showPlaceInfo(DtlPlace dtlPlace) {
        NavigationBuilder.create()
                .with(infoFragmentCompass)
                .data(dtlPlace)
                .move(Route.DTL_MAP_INFO);
    }

    @Override
    public void addPin(LatLng latLng, String id) {
        googleMap.addMarker(new MarkerOptions()
                .snippet(id)
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)));
    }

    @Override
    public void clearMap() {
        googleMap.clear();
    }

    @Override
    public void prepareInfoWindow(int height) {
        int ownHeight = getView().getHeight();
        int centerY = ownHeight / 2;
        int resultY = height + getResources().getDimensionPixelSize(R.dimen.spacing_huge);
        int offset = resultY - centerY;
        animateToMarker(selectedLocation, offset);
    }
}