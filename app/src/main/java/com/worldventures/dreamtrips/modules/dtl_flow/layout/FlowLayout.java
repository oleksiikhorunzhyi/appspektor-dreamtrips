package com.worldventures.dreamtrips.modules.dtl_flow.layout;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;

import com.crashlytics.android.Crashlytics;
import com.worldventures.dreamtrips.modules.dtl_flow.presenter.FlowPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.screen.FlowScreen;

import timber.log.Timber;

public abstract class FlowLayout<V extends FlowScreen, P extends FlowPresenter<V, ?>>
        extends InjectingLayout<V, P> implements FlowScreen {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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
