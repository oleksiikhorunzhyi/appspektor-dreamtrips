package com.messenger.ui.presenter;

import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface MessengerPresenter<V extends MvpView, S extends Parcelable> extends ViewStateMvpPresenter<V, S> {
   void onDisconnectedOverlayClicked();

   int getToolbarMenuRes();
   void onToolbarMenuPrepared(Menu menu);
   boolean onToolbarMenuItemClick(MenuItem item);
}
