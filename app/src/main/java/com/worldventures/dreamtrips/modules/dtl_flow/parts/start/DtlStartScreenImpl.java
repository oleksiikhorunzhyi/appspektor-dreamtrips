package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowLayout;

import javax.inject.Inject;

import butterknife.InjectView;
import timber.log.Timber;

public class DtlStartScreenImpl extends FlowLayout<DtlStartScreen, DtlStartPresenter, DtlStartPath>
        implements DtlStartScreen {

    private static final int REQUEST_CHECK_SETTINGS = 48151623;

    @Inject
    ActivityResultDelegate activityResultDelegate;
    //
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public void locationResolutionRequired(Status status) {
        try {
            status.startResolutionForResult((Activity) getContext(), REQUEST_CHECK_SETTINGS);
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

    public void activityResult(int requestCode, int resultCode) { // TODO :: 3/31/16
        if (requestCode == REQUEST_CHECK_SETTINGS) {
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
            activityResultDelegate.clear();
        }
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
