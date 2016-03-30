package com.worldventures.dreamtrips.modules.dtl_flow.presenter;

import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.screen.FlowScreen;

public abstract interface FlowPresenter<V extends FlowScreen, S extends Parcelable>
        extends ViewStateMvpPresenter<V, S> {

    int getToolbarMenuRes();

    void onToolbarMenuPrepared(Menu menu);

    boolean onToolbarMenuItemClick(MenuItem item);
}
