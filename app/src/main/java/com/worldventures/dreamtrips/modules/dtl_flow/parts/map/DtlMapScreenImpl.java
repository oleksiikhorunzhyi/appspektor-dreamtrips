package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.worldventures.dreamtrips.modules.dtl.helper.SearchViewHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info.DtlMapInfoScreenImpl;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;
import com.worldventures.dreamtrips.modules.map.renderer.DtClusterRenderer;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.trips.view.custom.ToucheableMapView;

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
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.sw_filter)
    SwitchCompat swHideDinings;
    @InjectView(R.id.redo_merchants)
    View loadMerchantsRoot;
    //
    LatLng selectedLocation;
    @State
    String lastQuery;
    //
    private SearchViewHelper searchViewHelper;
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
        inflateToolbarMenu(toolbar);
        //
        checkMapAvailable();
        prepareMap();
        prepareView();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mapView != null) {
            mapView.removeAllViews();
        }
        if (googleMap != null) {
            googleMap.clear();
            googleMap.setOnMarkerClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    protected void prepareView() {
        initToolbar();
        //
        MapViewUtils.setLocationButtonGravity(mapView, 16, RelativeLayout.ALIGN_PARENT_END, RelativeLayout.ALIGN_PARENT_BOTTOM);
    }

    protected void initToolbar() {
        if (isTabletLandscape()) {
            ButterKnife.findById(toolbar, R.id.spinnerStyledTitle).setVisibility(View.GONE);
            ButterKnife.findById(toolbar, R.id.locationModeCaption).setVisibility(View.GONE);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            toolbar.setNavigationOnClickListener(view -> ((FlowActivity) getActivity()).openLeftDrawer());
            toolbar.findViewById(R.id.titleContainer).setOnClickListener(v -> getPresenter().onLocationCaptionClick());
        }
    }

    private void checkMapAvailable() {
        if (isGooglePlayServicesAvailable()) {
            mapView.setVisibility(View.GONE);
            noGoogleContainer.setVisibility(View.VISIBLE);
        } else {
            mapView.onCreate(null);
            mapView.onResume();
            MapsInitializer.initialize(getContext());
        }
    }

    @Override
    public Observable<Boolean> getToggleObservable() {
        return RxCompoundButton.checkedChanges(swHideDinings)
                .compose(RxLifecycle.bindView(this));
    }

    protected void prepareMap() {
        mapView.getMapAsync(map -> {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);
            mapView.setMapTouchListener(this::onMapTouched);
            onMapLoaded();
        });
    }

    @OnClick(R.id.redo_merchants_button)
    public void onMechantsRedoClick() {
        getPresenter().onLoadMerchantsClick(googleMap.getCameraPosition().target);
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
        int ownHeight = getHeight() - ButterKnife.findById(this, R.id.filterToggleContainer).getBottom();
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
        swHideDinings.setChecked(hide);
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
    public void showPinInfo(String merchantId) {
        infoContainer.removeAllViews();
        PathContext newContext = PathContext.create((PathContext) getContext(), new DtlMapInfoPath(FlowUtil.currentMaster(this), merchantId), Path.contextFactory());
        DtlMapInfoScreenImpl infoView = (DtlMapInfoScreenImpl) LayoutInflater.from(getContext()).cloneInContext(newContext)
                .inflate(FlowUtil.layoutFrom(DtlMapInfoPath.class), infoContainer, false);
        infoView.setInjector(injector);
        infoContainer.addView(infoView);
    }

    @Override
    public void openFilter() {
        ((FlowActivity)getActivity()).openRightDrawer();
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
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation) {
        if (dtlLocation == null || toolbar == null) return; // for safety reasons
        // TODO :: where isTabletLandscape()) ?? return; // no showing in landscape
        //
        TextView locationTitle = ButterKnife.<TextView>findById(toolbar, R.id.spinnerStyledTitle);
        TextView locationModeCaption = ButterKnife.<TextView>findById(toolbar, R.id.locationModeCaption);
        //
        if (locationTitle == null || locationModeCaption == null)
            return; // for safety reasons on samsung
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

    private boolean isGooglePlayServicesAvailable() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext()) != ConnectionResult.SUCCESS;
    }

}
