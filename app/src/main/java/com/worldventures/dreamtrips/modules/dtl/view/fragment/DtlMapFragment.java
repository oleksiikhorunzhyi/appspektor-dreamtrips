package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlaceDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesMapBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapPresenter;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;
import com.worldventures.dreamtrips.modules.map.renderer.DtClusterRenderer;
import com.worldventures.dreamtrips.modules.map.view.MapFragment;

import butterknife.InjectView;
import icepick.State;

@Layout(R.layout.fragment_dtl_places_map)
public class DtlMapFragment extends MapFragment<DtlMapPresenter> implements DtlMapPresenter.View {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    //
    PlacesMapBundle bundle;
    @State
    LatLng selectedLocation;

    private ClusterManager<DtlClusterItem> clusterManager;

    @Override
    protected DtlMapPresenter createPresenter(Bundle savedInstanceState) {
        bundle = getArguments().getParcelable(ComponentPresenter.EXTRA_DATA);
        return new DtlMapPresenter(bundle);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        toolbar.inflateMenu(R.menu.menu_dtl_map);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_list:
                    router.moveTo(Route.DTL_PLACES_HOLDER, NavigationConfigBuilder.forFragment()
                            .data(bundle)
                            .fragmentManager(getFragmentManager())
                            .backStackEnabled(false)
                            .containerId(R.id.dtl_container)
                            .build());
                    return true;
                case R.id.action_dtl_filter:
                    ((MainActivity) getActivity()).openRightDrawer();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        });
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
        router.moveTo(Route.DTL_MAP_INFO, NavigationConfigBuilder.forRemoval()
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.container_info)
                .build());
    }

    @Override
    public void showPlaceInfo(DtlPlace dtlPlace) {
        router.moveTo(Route.DTL_MAP_INFO, NavigationConfigBuilder.forFragment()
                .containerId(R.id.container_info)
                .fragmentManager(getChildFragmentManager())
                .backStackEnabled(false)
                .data(new PlaceDetailsBundle(dtlPlace, bundle.isSlave()))
                .build());
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
        FragmentManager fragmentManager;
        if (!tabletAnalytic.isTabletLandscape() || !bundle.isSlave()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            fragmentManager = getFragmentManager();
            toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
            toolbar.findViewById(R.id.spinnerStyledTitle).setOnClickListener(v ->
                    router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
                            .backStackEnabled(false)
                            .containerId(R.id.dtl_container)
                            .fragmentManager(fragmentManager)
                            .build()));
            ((TextView) toolbar.findViewById(R.id.spinnerStyledTitle)).setText(location.getLongName());
        } else {
            toolbar.findViewById(R.id.spinnerStyledTitle).setVisibility(View.GONE);
        }
    }
}
