package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.crashlytics.android.Crashlytics;
import com.messenger.ui.view.layout.BaseViewStateLinearLayout;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.MonitoringHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import flow.path.Path;
import timber.log.Timber;

public abstract class DtlLayout<V extends DtlScreen, P extends DtlPresenter<V, ?>, T extends DtlPath>
        extends BaseViewStateLinearLayout<V, P> implements DtlScreen, InjectorHolder, PathView<T> {

    protected Injector injector;
    @Inject
    protected ActivityResultDelegate activityResultDelegate;

    public DtlLayout(Context context) {
        super(context);
    }

    public DtlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        MonitoringHelper.startInteractionName(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(VERTICAL);
        ButterKnife.inject(this);
    }

    @Override
    public void setInjector(Injector injector) {
        this.injector = injector;
        injector.inject(this);
    }

    @Deprecated
    @Override
    public void setPath(T path) {
        // it's so sad we don't have mortar
        // so have to postpone view manipulation
        // till every set* method is called
    }

    @Override
    public T getPath() {
        return Path.get(getContext());
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
    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(getContext()) && ViewUtils.isLandscapeOrientation(getContext());
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

    protected boolean inflateToolbarMenu(Toolbar toolbar) {
        if (getPresenter().getToolbarMenuRes() <= 0) {
            return false;
        }
        if (toolbar.getMenu() != null) {
            toolbar.getMenu().clear();
        }
        toolbar.inflateMenu(getPresenter().getToolbarMenuRes());
        getPresenter().onToolbarMenuPrepared(toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(getPresenter()::onToolbarMenuItemClick);
        return true;
    }
}
