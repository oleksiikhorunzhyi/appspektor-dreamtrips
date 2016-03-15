package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.LocationPresenter;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import permissions.dispatcher.DeniedPermission;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.ShowsRationale;
import rx.functions.Action1;
import timber.log.Timber;

@RuntimePermissions
@Layout(R.layout.fragment_add_location)
public class LocationFragment extends RxBaseFragmentWithArgs<LocationPresenter, Location> implements LocationPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 81680085;

    @Inject
    ActivityResultDelegate activityResultDelegate;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.input_location)
    EditText input;

    private Location obtainedLocation;

    @Override
    protected LocationPresenter createPresenter(Bundle savedInstanceState) {
        return new LocationPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        initToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityResult(activityResultDelegate.getRequestCode(),
                activityResultDelegate.getResultCode(), activityResultDelegate.getData());
    }

    private void fetchAndSetLocation() {
        getPresenter().getLocation().subscribe((Action1<Location>) location -> {
            obtainedLocation = location;
            input.setText(obtainedLocation.getName());
        });
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.menu_photo_tag_screen);
        toolbar.setTitle(R.string.add_location);
        toolbar.setOnMenuItemClickListener(this::onToolBarMenuItemClicked);
        toolbar.setNavigationContentDescription(R.string.back);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(v -> cancelClicked());
    }

    protected boolean onToolBarMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (getTargetFragment() instanceof Callback) {
                    SoftInputUtil.hideSoftInputMethod(getActivity());
                    ((Callback) getTargetFragment()).onLocationDone(composeLocation());
                }
                router.back();
        }
        return true;
    }

    private void fillLocationInput(){
        if (getArgs() != null && !TextUtils.isEmpty(getArgs().getName())) {
            input.setText(getArgs().getName());
        } else {
            fetchAndSetLocation();
        }
    }

    private Location composeLocation() {
        String name = input.getText().toString();
        Location result = new Location();
        if (obtainedLocation != null){
            if (!name.equals(obtainedLocation.getName())){
                obtainedLocation.setName(name);
                obtainedLocation.setLng(0);
                obtainedLocation.setLat(0);
            }
            result = obtainedLocation;
        } else if (!TextUtils.isEmpty(name)){
            result.setName(name);
        }
        return result;
    }

    @OnClick(R.id.clear_location)
    void clearLocation() {
        input.setText(null);
    }

    private void cancelClicked() {
        router.back();
    }

    private void activityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getPresenter().onPermissionGranted();
                        fillLocationInput();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        getPresenter().locationNotGranted();
                        break;
                    default:
                        break;
                }
                activityResultDelegate.clear();
                break;
        }
    }

    @Override
    public void checkPermissions() {
        LocationFragmentPermissionsDispatcher.locationPermissionWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void resolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening edit location.");
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void locationPermission() {
        getPresenter().onPermissionGranted();
    }

    @ShowsRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation() {
        Snackbar.make(getView(), R.string.permission_location_rationale, Snackbar.LENGTH_SHORT).show();
    }

    @DeniedPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        Snackbar.make(getView(), R.string.no_location_permission, Snackbar.LENGTH_SHORT).show();
    }

    public interface Callback {
        void onLocationDone(Location location);
    }
}
