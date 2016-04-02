package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlLocationsBundle;
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
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;
import timber.log.Timber;

@Layout(R.layout.fragment_dtl_merchant_map)
public class DtlMapFragment extends MapFragment<DtlMapPresenter> implements DtlMapPresenter.View {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.sw_filter)
    SwitchCompat swHideDinings;
    @InjectView(R.id.redo_merchants)
    View loadMerchantsRoot;

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
    private Marker locationPin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        MapViewUtils.setLocationButtonGravity(mapView, 16, RelativeLayout.ALIGN_PARENT_END, RelativeLayout.ALIGN_PARENT_BOTTOM);
        //
        toolbar.inflateMenu(R.menu.menu_dtl_map);
        searchViewHelper = new SearchViewHelper();
        searchViewHelper.init(toolbar.getMenu().findItem(R.id.action_search), lastQuery, query -> {
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
        //
        initToolbar();
        swHideDinings.setOnCheckedChangeListener((buttonView, isChecked) -> getPresenter().onCheckHideDinings(isChecked));
    }

    @Override
    public void addLocationMarker(LatLng location) {
        if (locationPin != null) locationPin.remove();
        locationPin = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pin))
                .position(location));
    }

    @OnClick(R.id.redo_merchants_button)
    public void onMechantsRedoClick() {
        getPresenter().onLoadMerchantsClick(googleMap.getCameraPosition().target);
    }

    @Override
    protected boolean onMarkerClick(Marker marker) {
        //not needed
        return false;
    }

    @Override
    public void hideDinings(boolean hide) {
        swHideDinings.setChecked(hide);
    }

    @Override
    public void showProgress(boolean show) {
        int textResId = show ? R.string.loading : R.string.dtl_load_merchants_here_button_caption;
        int visibility = show ? View.VISIBLE : View.GONE;
        //
        Button loadMerchantsBtn = ButterKnife.<Button>findById(loadMerchantsRoot, R.id.redo_merchants_button);
        ButterKnife.findById(loadMerchantsRoot, R.id.redo_merchants_progress).setVisibility(visibility);
        //
        loadMerchantsBtn.setText(textResId);
        loadMerchantsBtn.setEnabled(!show);
    }

    @Override
    public GoogleMap getMap() {
        return googleMap;
    }

    @Override
    public void cameraPositionChange(CameraPosition cameraPosition) {
        clusterManager.onCameraChange(cameraPosition);
        selectedLocation = cameraPosition.target;
    }

    @Override
    public void markerClick(Marker marker) {
        clusterManager.onMarkerClick(marker);
    }

    @Override
    public void showButtonLoadMerchants(boolean show) {
        loadMerchantsRoot.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void zoom(float zoom) {
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    @Override
    public void informUser(int stringId) {
        if (isAdded() && getView() != null)
            try {
                Snackbar.make(getView(), stringId, Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }
    }

    @Override
    public void tryHideMyLocationButton(boolean hide) {
        googleMap.setMyLocationEnabled(!hide);
    }

    @Override
    protected void onMapLoaded() {
        clusterManager = new ClusterManager<>(getActivity(), googleMap);
        clusterManager.setRenderer(new DtClusterRenderer(getActivity().getApplicationContext(), googleMap, clusterManager));

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
                    googleMap.getCameraPosition().zoom + 1.0f), MapViewUtils.MAP_ANIMATION_DURATION, null);

            return true;
        });
        //
        getPresenter().onMapLoaded();
    }

    @Override
    public void centerIn(Location location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.asLatLng(), MapViewUtils.DEFAULT_ZOOM));
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
        hideInfoIfShown();
        getFragmentManager().popBackStack();
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
        int ownHeight = getView().getHeight() - ButterKnife.findById(getView(), R.id.filterToggleContainer).getBottom();
        int centerY = ownHeight / 2;
        int resultY = height + getResources().getDimensionPixelSize(R.dimen.size_huge);
        int offset = resultY - centerY;
        if (selectedLocation != null) animateToMarker(selectedLocation, offset);
    }

    @Override
    public void onDestroyView() {
        if (searchViewHelper != null) searchViewHelper.dropHelper();
        //
        if (clusterManager != null) {
            clusterManager.setOnClusterClickListener(null);
            clusterManager.setOnClusterItemClickListener(null);
        }
        //
        super.onDestroyView();
    }

    public void initToolbar() {
        if (!tabletAnalytic.isTabletLandscape() || !bundle.isSlave()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
            toolbar.findViewById(R.id.titleContainer).setOnClickListener(v -> {
                hideInfoIfShown();
                router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
                        .backStackEnabled(true)
                        .data(new DtlLocationsBundle())
                        .containerId(R.id.dtl_container)
                        .fragmentManager(getFragmentManager())
                        .build());
            });
        } else {
            toolbar.findViewById(R.id.spinnerStyledTitle).setVisibility(View.GONE);
            toolbar.findViewById(R.id.locationModeCaption).setVisibility(View.GONE);
        }
    }

    @Override
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation) {
        if (dtlLocation == null || toolbar == null || // for safety reasons
                isTabletLandscape()) return; // no showing in landscape
        //
        TextView locationTitle = ButterKnife.<TextView>findById(toolbar, R.id.spinnerStyledTitle);
        TextView locationModeCaption = ButterKnife.<TextView>findById(toolbar, R.id.locationModeCaption);
        //
        if (locationTitle == null || locationModeCaption == null) return; // for safety reasons on samsung
        //
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
            case EXTERNAL:
                locationTitle.setText(dtlLocation.getLongName());
                locationModeCaption.setVisibility(View.GONE);
                break;
            case FROM_MAP:
                if (dtlLocation.getLongName() == null) {
                    locationModeCaption.setVisibility(View.GONE);
                    locationTitle.setText(R.string.dtl_nearby_caption);
                } else {
                    locationModeCaption.setVisibility(View.VISIBLE);
                    locationTitle.setText(dtlLocation.getLongName());
                }
                break;
        }
    }
}
