package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
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
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.LocationPresenter;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import icepick.Icepick;
import icepick.State;
import rx.functions.Action1;
import timber.log.Timber;

@Layout(R.layout.fragment_add_location)
public class LocationFragment extends RxBaseFragmentWithArgs<LocationPresenter, Location> implements LocationPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 65001;

    @Inject ActivityResultDelegate activityResultDelegate;
    @Inject PermissionDispatcher permissionDispatcher;

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.input_location) EditText input;
    @InjectView(R.id.progress) View progress;

    @State Location obtainedLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

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
        if (obtainedLocation != null && !TextUtils.isEmpty(obtainedLocation.getName())) {
            setInputLocation(obtainedLocation.getName());
        } else if (getArgs() != null && !TextUtils.isEmpty(getArgs().getName())) {
            setInputLocation(getArgs().getName());
        } else if (getPresenter().isGpsOn()) {
            fetchAndSetLocation();
        } else {
            activityResult(activityResultDelegate.getRequestCode(),
                    activityResultDelegate.getResultCode(), activityResultDelegate.getData());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        obtainedLocation = obtainedLocation == null ? new Location() : obtainedLocation;
        obtainedLocation.setName(input.getText().toString());
        Icepick.saveInstanceState(this, outState);
    }

    private void fetchAndSetLocation() {
        if (!TextUtils.isEmpty(input.getText())) return;
        showProgress();
        bind(getPresenter().getLocation())
                .subscribe((Action1<Location>) location -> {
                    if (location != null) {
                        obtainedLocation = location;
                        setInputLocation(obtainedLocation.getName());
                    }
                }, e -> {});
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.menu_add_location_screen);
        toolbar.setTitle(R.string.add_location);
        toolbar.setOnMenuItemClickListener(this::onToolBarMenuItemClicked);
        toolbar.setNavigationContentDescription(R.string.back);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(v -> cancelClicked());
        setToolbarAlpha(255);
    }

    private void setToolbarAlpha(int percentage) {
        toolbar.getBackground().mutate().setAlpha(percentage);
    }

    protected boolean onToolBarMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                SoftInputUtil.hideSoftInputMethod(getActivity());
                getPresenter().onDone(composeLocation());
                router.back();
        }
        return true;
    }

    private void setInputLocation(String location) {
        input.setText(location);
    }

    private Location composeLocation() {
        String name = input.getText().toString();
        Location result = new Location();
        if (obtainedLocation != null) {
            if (!name.equals(obtainedLocation.getName())) {
                obtainedLocation.setName(name);
                obtainedLocation.setLng(0);
                obtainedLocation.setLat(0);
            }
            result = obtainedLocation;
        } else if (!TextUtils.isEmpty(name)) {
            result.setName(name);
        }
        return result;
    }

    @OnTextChanged(value = R.id.input_location, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void onBeforeTextChanged(CharSequence text) {
        if (input.getText().length() == getResources().getInteger(R.integer.social_location_name_max_length))
            informUser(R.string.location_name_length_message);
    }

    @OnTextChanged(R.id.input_location)
    void onTextChanged(CharSequence text) {
        if (text.length() == 1) getPresenter().stopDetectLocation();
    }

    @OnClick(R.id.clear_location)
    void clearLocation() {
        if (obtainedLocation != null) {
            obtainedLocation.setName("");
            obtainedLocation.setLng(0);
            obtainedLocation.setLat(0);
        }
        setInputLocation(null);
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
                        if (getPresenter().isGpsOn()) {
                            getPresenter().onPermissionGranted();
                            fetchAndSetLocation();
                        } else {
                            getPresenter().locationNotGranted();
                        }
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
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
        toolbar.getMenu().findItem(R.id.action_done).setEnabled(false);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.action_done).setEnabled(true);
    }

    @Override
    public void checkPermissions() {
        permissionDispatcher
                .requestPermission(PermissionConstants.LOCATION_PERMISSIONS)
                .compose(this::bind)
                .subscribe(new PermissionSubscriber()
                        .onPermissionDeniedAction(this::showDeniedForLocation)
                        .onPermissionGrantedAction(this::locationPermissionGranted)
                        .onPermissionRationaleAction(this::showRationaleForLocation));
    }

    @Override
    public void resolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening edit location.");
        }
    }

    void locationPermissionGranted() {
        getPresenter().onPermissionGranted();
    }

    void showRationaleForLocation() {
        Snackbar.make(getView(), R.string.permission_location_rationale, Snackbar.LENGTH_SHORT).show();
    }

    void showDeniedForLocation() {
        Snackbar.make(getView(), R.string.no_location_permission, Snackbar.LENGTH_SHORT).show();
    }
}
