package com.worldventures.dreamtrips.modules.dtl_flow;

import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;

public abstract class FlowPresenterImpl<V extends FlowScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements FlowPresenter<V, S> {

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
}
