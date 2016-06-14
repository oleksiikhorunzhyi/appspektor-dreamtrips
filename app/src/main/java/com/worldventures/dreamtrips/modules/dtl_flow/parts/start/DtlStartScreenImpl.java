package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;
import timber.log.Timber;

public class DtlStartScreenImpl extends DtlLayout<DtlStartScreen, DtlStartPresenter, DtlStartPath>
        implements DtlStartScreen, ActivityResultDelegate.ActivityResultListener {

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        injector.inject(this);
        activityResultDelegate.addListener(this);
    }

    @Override
    public void locationResolutionRequired(Status status) {
        AppCompatActivity appCompatActivity = getActivity();
        if (appCompatActivity == null) {
            Timber.i("AppCompatActivity is null");
            return;
        } try {
            status.startResolutionForResult(appCompatActivity, DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
            Crashlytics.logException(e);
            Timber.e(e, "Error opening settings activity.");
        }
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getPresenter().onLocationResolutionGranted();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    getPresenter().onLocationResolutionDenied();
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        activityResultDelegate.removeListener(this);
        super.onDetachedFromWindow();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Boilerplate stuff
    ///////////////////////////////////////////////////////////////////////////

    public DtlStartScreenImpl(Context context) {
        super(context);
    }

    public DtlStartScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlStartPresenter createPresenter() {
        return new DtlStartPresenterImpl(getContext(), injector);
    }
}
