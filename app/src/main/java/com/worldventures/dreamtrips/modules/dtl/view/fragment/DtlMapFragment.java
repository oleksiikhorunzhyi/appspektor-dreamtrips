package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlacesToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapPresenter;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;
import com.worldventures.dreamtrips.modules.map.renderer.DtClusterRenderer;
import com.worldventures.dreamtrips.modules.map.view.MapFragment;

import icepick.State;

@Layout(R.layout.fragment_dtl_places_map)
@MenuResource(R.menu.menu_dtl_map)
public class DtlMapFragment extends MapFragment<DtlMapPresenter> implements DtlMapPresenter.View {

    PlacesBundle bundle;
    @State
    LatLng selectedLocation;
    //
    DtlPlacesToolbarHelper toolbarHelper;
    FragmentCompass infoFragmentCompass;

    private ClusterManager<DtlClusterItem> clusterManager;

    @Override
    protected DtlMapPresenter createPresenter(Bundle savedInstanceState) {
        bundle = getArguments().getParcelable(ComponentPresenter.EXTRA_DATA);
        return new DtlMapPresenter(bundle);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        toolbarHelper = new DtlPlacesToolbarHelper(getActivity(), fragmentCompass, eventBus);
        toolbarHelper.attach(rootView);
        toolbarHelper.inflateMenu(R.menu.menu_dtl_map, item -> {
            switch (item.getItemId()) {
                case R.id.action_list:
                    NavigationBuilder.create().with(fragmentCompass)
                            .data(bundle)
                            .move(Route.DTL_PLACES_LIST);
                    return true;
                case R.id.action_dtl_filter:
                    ((MainActivity) getActivity()).openRightDrawer();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        });
        infoFragmentCompass = new FragmentCompass(getActivity(), R.id.container_info);
        infoFragmentCompass.setFragmentManager(getChildFragmentManager());
        fragmentCompass.setFragmentManager(getFragmentManager());
        fragmentCompass.setContainerId(R.id.dtl_container);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        toolbarHelper.onPause();
    }

    @Override
    protected boolean onMarkerClick(Marker marker) {
        //not needed
        return false;
    }

    @Override
    protected void onMapLoaded() {
        clusterManager = new ClusterManager<>(getActivity(), googleMap);
        clusterManager.setRenderer(new DtClusterRenderer(getActivity().getApplicationContext(),
                googleMap, clusterManager));
        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterItemClickListener(dtlClusterItem -> {
            selectedLocation = dtlClusterItem.getPosition();
            getPresenter().onMarkerClick(dtlClusterItem.getId());
            return true;
        });

        clusterManager.setOnClusterClickListener(cluster -> {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                    googleMap.getCameraPosition().zoom + 1.0f));
            return true;
        });

        getPresenter().onMapLoaded();
    }

    @Override
    public void centerIn(DtlLocation location) {
        LatLng latLng = new LatLng(location.getCoordinates().getLat(), location.getCoordinates().getLng());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
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
    public void addPin(String id, LatLng latLng, DtlPlaceType type) {
        clusterManager.addItem(new DtlClusterItem(id, latLng, type));
    }

    @Override
    public void clearMap() {
        googleMap.clear();
    }

    @Override
    public void prepareInfoWindow(int height) {
        int ownHeight = getView().getHeight();
        int centerY = ownHeight / 2;
        int resultY = height + getResources().getDimensionPixelSize(R.dimen.size_huge);
        int offset = resultY - centerY;
        animateToMarker(selectedLocation, offset);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (clusterManager != null) {
            clusterManager.setOnClusterClickListener(null);
            clusterManager.setOnClusterItemClickListener(null);
        }
    }

    @Override
    public void initToolbar(DtlLocation location) {
        toolbarHelper.setPlaceForToolbar(location);
    }
}
