package com.worldventures.dreamtrips.modules.picklocation.view;

import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.hannesdorfmann.mosby.mvp.layout.MvpLinearLayout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.picklocation.presenter.PickLocationPresenter;
import com.worldventures.dreamtrips.modules.picklocation.presenter.PickLocationPresenterImpl;
import com.worldventures.dreamtrips.modules.picklocation.presenter.LocationPickerToolbarPresenter;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationPermissionHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.State;

public class PickLocationViewImpl extends MvpLinearLayout<PickLocationView, PickLocationPresenter>
        implements PickLocationView {

    public static final float DEFAULT_MAP_ZOOM = 15.0f;

    @InjectView(R.id.pick_location_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.pick_location_no_play_services)
    View noPlayServicesOverlay;

    private LocationPickerToolbarPresenter toolbarPresenter;

    private MapFragment mapFragment;
    private GoogleMap map;

    @State
    boolean isCurrentLocationSet;

    @Inject
    FragmentManager fragmentManager;

    // save for presenter
    private LocationPermissionHelper locationPermissionHelper;

    public PickLocationViewImpl(Context context) {
        super(context);
        init();
    }

    public PickLocationViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.screen_pick_location, this, true);
        ButterKnife.inject(this, this);
        ((Injector)getContext()).inject(this);
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.pick_location_map_fragment);
        toolbarPresenter = new LocationPickerToolbarPresenter(toolbar, getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        toolbarPresenter.setTitle(R.string.location_picker_title);
        toolbarPresenter.enableUpNavigationButton();
        toolbar.inflateMenu(getPresenter().getToolbarMenuRes());
        toolbar.setOnMenuItemClickListener(getPresenter()::onMenuItemClick);

        getPresenter().onShouldInitMap();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (map != null) map.clear();
    }

    @Override
    public PickLocationPresenter createPresenter() {
        return new PickLocationPresenterImpl(getContext(), locationPermissionHelper);
    }

    @Override
    public void showRationalForLocationPermission() {
        Snackbar.make(this, R.string.permission_location_rationale,
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showDeniedLocationPermissionError() {
        Snackbar.make(this, R.string.no_location_permission,
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void initMap() {
        noPlayServicesOverlay.setVisibility(View.GONE);
        mapFragment.getMapAsync(map -> {
            this.map = map;
            MapsInitializer.initialize(getContext());
            map.setMyLocationEnabled(true);
            MapViewUtils.setLocationButtonGravity(mapFragment.getView(), 16, RelativeLayout.ALIGN_PARENT_END,
                    RelativeLayout.ALIGN_PARENT_BOTTOM);
            getPresenter().onMapInitialized(map);
        });
    }

    @Override
    public void setCurrentLocation(Location location, boolean animated) {
        isCurrentLocationSet = true;
        LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_MAP_ZOOM);
        if (animated) {
            map.animateCamera(cameraUpdate);
        } else {
            map.moveCamera(cameraUpdate);
        }
    }

    @Override
    public boolean isCurrentLocationSet() {
        return isCurrentLocationSet;
    }

    @Override
    public void showPlayServicesAbsentOverlay() {
        noPlayServicesOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void showObtainLocationError() {
        Snackbar.make(this, R.string.location_picker_could_not_get_location, Snackbar.LENGTH_SHORT);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    public void setLocationPermissionHelper(LocationPermissionHelper locationPermissionHelper) {
        this.locationPermissionHelper = locationPermissionHelper;
    }
}
