package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

import timber.log.Timber;

public abstract class FlowLayout<V extends FlowScreen, P extends FlowPresenter<V, ?>, T extends StyledPath>
        extends PathLayout<V, P, T> implements FlowScreen, ApiErrorView {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepared() {
        super.onPrepared();
        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    public void onApiCallFailed() {
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    public void hideSoftInput() {
        SoftInputUtil.hideSoftInputMethod(this);
    }

    @Override
    public void informUser(String message) {
        try {
            Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Crashlytics.logException(e);
            Timber.e(e, "Exception during showing snackbar to user");
        }
    }

    @Override
    public void informUser(@StringRes int stringResId) {
        try {
            Snackbar.make(this, stringResId, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Crashlytics.logException(e);
            Timber.e(e, "Exception during showing snackbar to user");
        }
    }
}
