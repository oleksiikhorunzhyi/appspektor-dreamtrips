package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

import javax.inject.Inject;

import timber.log.Timber;

public abstract class FlowLayout<V extends FlowScreen, P extends FlowPresenter<V, ?>, T extends StyledPath>
        extends PathLayout<V, P, T> implements FlowScreen, ApiErrorView {

    @Inject
    protected ActivityResultDelegate activityResultDelegate;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepared() {
        injector.inject(this);
        super.onPrepared();
        setOrientation(LinearLayout.VERTICAL);
    }

    @Nullable
    protected AppCompatActivity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
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
