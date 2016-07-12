package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.ApiErrorPresenter;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public abstract class DtlPresenterImpl<V extends DtlScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements DtlPresenter<V, S> {

    @Inject
    @Global
    protected EventBus eventBus;
    @Inject
    protected AnalyticsInteractor analyticsInteractor;

    protected ApiErrorPresenter apiErrorPresenter;

    protected Context context;

    public DtlPresenterImpl(Context context) {
        this.context = context;
        apiErrorPresenter = new ApiErrorPresenter();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getToolbarMenuRes() {
        return 0;
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onNewViewState() {
    }

    @Override
    public void applyViewState() {
    }

    @Override
    @CallSuper
    public void onAttachedToWindow() {
        try {
            eventBus.registerSticky(this);
        } catch (Exception ignored) {
            Timber.v("EventBus :: Problem on registering sticky - no \'onEvent' method found in " + getClass().getName());
        }
    }

    @Override
    @CallSuper
    public void onDetachedFromWindow() {
        apiErrorPresenter.dropView();
        if (EventBus.getDefault().isRegistered(this)) eventBus.unregister(this);
    }
}
