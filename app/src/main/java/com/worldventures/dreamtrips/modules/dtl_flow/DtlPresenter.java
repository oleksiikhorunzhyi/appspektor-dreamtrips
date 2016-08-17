package com.worldventures.dreamtrips.modules.dtl_flow;

import android.os.Parcelable;
import android.support.annotation.MenuRes;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.presenter.ViewStateMvpPresenter;

public interface DtlPresenter<V extends DtlScreen, S extends Parcelable> extends ViewStateMvpPresenter<V, S> {

   @MenuRes
   int getToolbarMenuRes();

   void onToolbarMenuPrepared(Menu menu);

   boolean onToolbarMenuItemClick(MenuItem item);
}
