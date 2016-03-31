package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.ApiErrorPresenter;

public abstract class FlowPresenterImpl<V extends FlowScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements FlowPresenter<V, S> {

    protected ApiErrorPresenter apiErrorPresenter;

    protected Context context;

    public FlowPresenterImpl(Context context) {
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
    public void onDetachedFromWindow() {
        apiErrorPresenter.dropView();
        super.onDetachedFromWindow();
    }
}
