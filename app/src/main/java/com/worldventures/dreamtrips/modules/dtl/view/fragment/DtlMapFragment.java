package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.SearchViewHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapPresenter;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;
import com.worldventures.dreamtrips.modules.map.renderer.DtClusterRenderer;
import com.worldventures.dreamtrips.modules.map.view.MapFragment;

import butterknife.InjectView;
import icepick.State;

@Layout(R.layout.fragment_dtl_merchant_map)
public class DtlMapFragment extends MapFragment<DtlMapPresenter> implements DtlMapPresenter.View {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    SearchViewHelper searchViewHelper;
    //
    DtlMapBundle bundle;
    //
    @State
    LatLng selectedLocation;
    @State
    String lastQuery;
    //
    private ClusterManager<DtlClusterItem> clusterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraAnimationDuration = 400;
        bundle = getArguments().getParcelable(ComponentPresenter.EXTRA_DATA);
    }

    @Override
    protected DtlMapPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMapPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // monkey patch to prevent opening this over proper landscape layout
        // after rotating from portrait
        if (!bundle.isSlave() && isTabletLandscape()) {
            navigateBack();
            return;
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        toolbar.inflateMenu(R.menu.menu_dtl_map);
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        searchViewHelper = new SearchViewHelper();
        searchViewHelper. init(searchItem, lastQuery, query -> {
            lastQuery = query;
            getPresenter().applySearch(query);
        });
        //
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_list:
                    navigateBack();
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
        //
        clusterManager.setOnClusterClickListener(cluster -> {
            if (googleMap.getCameraPosition().zoom >= 17.0f) {
                selectedLocation = cluster.getPosition();
                getPresenter().onMarkerClick(Queryable.from(cluster.getItems()).first().getId());
            } else googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                    googleMap.getCameraPosition().zoom + 1.0f), cameraAnimationDuration, null);

            return true;
        });
        //
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
    public void showMerchantInfo(String merchantId) {
        router.moveTo(Route.DTL_MAP_INFO, NavigationConfigBuilder.forFragment()
                .containerId(R.id.container_info)
                .fragmentManager(getChildFragmentManager())
                .backStackEnabled(false)
                .data(new DtlMerchantDetailsBundle(merchantId, bundle.isSlave()))
                .build());
    }

    private void navigateBack() {
        router.moveTo(Route.DTL_MERCHANTS_HOLDER, NavigationConfigBuilder.forFragment()
                .fragmentManager(getFragmentManager())
                .backStackEnabled(false)
                .clearBackStack(true)
                .containerId(R.id.dtl_container)
                .build());
    }

    @Override
    public void addPin(String id, LatLng latLng, DtlMerchantType type) {
        clusterManager.addItem(new DtlClusterItem(id, latLng, type));
    }

    @Override
    public void clearMap() {
        clusterManager.clearItems();
    }

    @Override
    public void renderPins() {
        clusterManager.cluster();
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
        searchViewHelper.dropHelper();
        //
        if (clusterManager != null) {
            clusterManager.setOnClusterClickListener(null);
            clusterManager.setOnClusterItemClickListener(null);
        }
        //
        super.onDestroyView();
    }

    @Override
    public void initToolbar(DtlLocation location) {
        if (!tabletAnalytic.isTabletLandscape() || !bundle.isSlave()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
            toolbar.findViewById(R.id.spinnerStyledTitle).setOnClickListener(v ->
                    router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
                            .backStackEnabled(false)
                            .containerId(R.id.dtl_container)
                            .fragmentManager(getFragmentManager())
                            .build()));
            ((TextView) toolbar.findViewById(R.id.spinnerStyledTitle)).setText(location.getLongName());
        } else {
            toolbar.findViewById(R.id.spinnerStyledTitle).setVisibility(View.GONE);
        }
    }
}
