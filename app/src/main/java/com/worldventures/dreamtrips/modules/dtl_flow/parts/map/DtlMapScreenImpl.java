package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoScreenImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlFilterButton;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;
import com.worldventures.dreamtrips.modules.map.renderer.DtClusterRenderer;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import flow.path.Path;
import flow.path.PathContext;
import icepick.State;
import rx.Observable;

public class DtlMapScreenImpl extends DtlLayout<DtlMapScreen, DtlMapPresenter, DtlMapPath>
        implements DtlMapScreen {

    protected static final int CAMERA_DURATION = 1000;

    @InjectView(R.id.map)
    ToucheableMapView mapView;
    @InjectView(R.id.container_info)
    FrameLayout infoContainer;
    @InjectView(R.id.container_no_google)
    FrameLayout noGoogleContainer;
    @InjectView(R.id.dtlToolbar)
    DtlToolbar dtlToolbar;
    @InjectView(R.id.filterDiningsSwitch)
    SwitchCompat filterDiningsSwitch;
    @InjectView(R.id.redo_merchants)
    View loadMerchantsRoot;
    @InjectView(R.id.filterBarRoot)
    View filterBarLayout;
    @InjectView(R.id.dtlfb_rootView)
    DtlFilterButton filtersButton;
    //
    LatLng selectedLocation;
    @State
    String lastQuery;
    //
    private ClusterManager<DtlClusterItem> clusterManager;
    private Marker locationPin;
    private GoogleMap googleMap;

    public DtlMapScreenImpl(Context context) {
        super(context);
    }

    public DtlMapScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlMapPresenter createPresenter() {
        return new DtlMapPresenterImpl(getContext(), injector);
    }

    @Override
    protected void onPostAttachToWindowView() {
        checkMapAvailable();
        prepareMap();
        prepareView();
    }

    @Override
    protected void onDetachedFromWindow() {
        destroyMap();
        super.onDetachedFromWindow();
    }

    protected void prepareView() {
        initToolbar();
        initToggle();
        //
        MapViewUtils.setLocationButtonGravity(mapView, 16, RelativeLayout.ALIGN_PARENT_END, RelativeLayout.ALIGN_PARENT_BOTTOM);
    }

    private void initToggle() {
        int visibility = isTabletLandscape() ? View.GONE : VISIBLE;
        filterBarLayout.setVisibility(visibility);
    }

    protected void initToolbar() {
        RxDtlToolbar.actionViewClicks(dtlToolbar)
                .throttleFirst(250L, TimeUnit.MILLISECONDS)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aVoid -> ((FlowActivity) getActivity()).openLeftDrawer());
        RxDtlToolbar.merchantSearchTextChanges(dtlToolbar)
                .skip(1)
                .debounce(250L, TimeUnit.MILLISECONDS)
                .filter(s -> !dtlToolbar.isCollapsed())
                .compose(RxLifecycle.bindView(this))
                .subscribe(getPresenter()::applySearch);
        RxDtlToolbar.locationInputFocusChanges(dtlToolbar)
                .skip(1)
                .compose(RxLifecycle.bindView(this))
                .filter(Boolean::booleanValue) // only true -> only focus gains
                .subscribe(aBoolean -> getPresenter().locationChangeRequested());
        RxDtlToolbar.navigationClicks(dtlToolbar)
                .throttleFirst(200L, TimeUnit.MILLISECONDS)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aVoid -> getPresenter().onListClicked());
    }

    @Override
    public void setFilterButtonState(boolean enabled) {
        filtersButton.setFilterEnabled(enabled);
    }

    private void checkMapAvailable() {
        if (MapsInitializer.initialize(getContext()) == 0) {
            mapView.onCreate(null);
            mapView.onResume();
        } else {
            mapView.setVisibility(View.GONE);
            noGoogleContainer.setVisibility(View.VISIBLE);
        }
    }

    protected void prepareMap() {
        mapView.getMapAsync(map -> {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);
            mapView.setMapTouchListener(this::onMapTouched);
            onMapLoaded();
        });
    }

    private void destroyMap() {
        if (googleMap != null) {
            googleMap = null;
        }
        if (mapView != null) {
            mapView.onPause();
            mapView.onDestroy();
        }
    }

    @Override
    public Observable<Boolean> getToggleObservable() {
        return RxCompoundButton.checkedChanges(filterDiningsSwitch)
                .compose(RxLifecycle.bindView(this));
    }

    @OnClick(R.id.redo_merchants_button)
    public void onMechantsRedoClick() {
        getPresenter().onLoadMerchantsClick(googleMap.getCameraPosition().target);
    }

    @OnClick(R.id.dtlfb_rootView)
    void onFiltersCounterClicked(View view) {
        ((FlowActivity) getActivity()).openRightDrawer();
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
    public boolean isToolbarCollapsed() {
        return dtlToolbar.isCollapsed();
    }

    @Override
    public void addLocationMarker(LatLng location) {
        if (locationPin != null) locationPin.remove();
        locationPin = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pin))
                .position(location));
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
    public void prepareInfoWindow(int height) {
        int ownHeight = getHeight() - ButterKnife.findById(this, R.id.filterBarRoot).getBottom();
        int centerY = ownHeight / 2;
        int resultY = height + getResources().getDimensionPixelSize(R.dimen.size_huge);
        int offset = resultY - centerY;
        animateTo(selectedLocation, offset);
    }

    @Override
    public void centerIn(Location location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.asLatLng(), MapViewUtils.DEFAULT_ZOOM));
    }

    @Override
    public void renderPins() {
        clusterManager.cluster();
    }

    @Override
    public void hideDinings(boolean hide) {
        filterDiningsSwitch.setChecked(hide);
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
    public void showPinInfo(DtlMerchant merchant) {
        infoContainer.removeAllViews();
        PathContext newContext = PathContext.create((PathContext) getContext(),
                new DtlMapInfoPath(FlowUtil.currentMaster(this), merchant), Path.contextFactory());
        DtlMapInfoScreenImpl infoView =
                (DtlMapInfoScreenImpl) LayoutInflater.from(getContext()).cloneInContext(newContext)
                .inflate(FlowUtil.layoutFrom(DtlMapInfoPath.class), infoContainer, false);
        infoView.setInjector(injector);
        infoContainer.addView(infoView);
    }

    @Override
    public void openFilter() {
        ((FlowActivity) getActivity()).openRightDrawer();
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
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation,
                                   @Nullable String actualSearchQuery) {
        if (dtlLocation == null) return;
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
            case EXTERNAL:
                dtlToolbar.setToolbarCaptions(actualSearchQuery, dtlLocation.getLongName());
                break;
            case FROM_MAP:
                String locationTitle = TextUtils.isEmpty(dtlLocation.getLongName()) ?
                        getResources().getString(R.string.dtl_nearby_caption_empty) :
                        getResources().getString(R.string.dtl_nearby_caption_format,
                                dtlLocation.getLongName());
                dtlToolbar.setToolbarCaptions(actualSearchQuery, locationTitle);
                break;
        }
    }

    @Override
    public void tryHideMyLocationButton(boolean hide) {
        googleMap.setMyLocationEnabled(!hide);
    }

    @Override
    public void animateTo(LatLng coordinates, int offset) {
        Projection projection = googleMap.getProjection();
        Point screenLocation = projection.toScreenLocation(coordinates);
        screenLocation.y -= offset;
        LatLng offsetTarget = projection.fromScreenLocation(screenLocation);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(offsetTarget), CAMERA_DURATION, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                onMarkerFocused();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void onMapLoaded() {
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setRenderer(new DtClusterRenderer(getContext().getApplicationContext(), googleMap, clusterManager));
        //
        clusterManager.setOnClusterItemClickListener(dtlClusterItem -> {
            selectedLocation = dtlClusterItem.getPosition();
            getPresenter().onMarkerClick(dtlClusterItem.getId());
            return true;
        });
        //
        clusterManager.setOnClusterClickListener(cluster -> {
            if (googleMap.getCameraPosition().zoom >= 17.0f) {
                getPresenter().onMarkerClick(Queryable.from(cluster.getItems()).first().getId());
            } else googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                    googleMap.getCameraPosition().zoom + 1.0f), MapViewUtils.MAP_ANIMATION_DURATION, null);

            return true;
        });
        //
        getPresenter().onMapLoaded();
    }

    private void onMapTouched() {
        hideInfoIfShown();
    }

    private void hideInfoIfShown() {
        infoContainer.removeAllViews();
    }

    protected void onMarkerFocused() {
        EventBus.getDefault().post(new DtlShowMapInfoEvent());
    }

}
