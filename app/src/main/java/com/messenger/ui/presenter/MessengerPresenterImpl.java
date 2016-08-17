package com.messenger.ui.presenter;

import android.content.Context;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.view.layout.MessengerScreen;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;

public abstract class MessengerPresenterImpl<V extends MessengerScreen, S extends Parcelable> extends BaseViewStateMvpPresenter<V, S> implements MessengerPresenter<V, S> {

   protected Context context;
   private Injector injector;

   protected Observable<SyncStatus> connectionStatusStream;
   protected SyncStatus currentConnectivityStatus = SyncStatus.DISCONNECTED;
   @Inject MessengerConnector messengerConnector;

   public MessengerPresenterImpl(Context context, Injector injector) {
      this.context = context;
      this.injector = injector;
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      connectionStatusStream = messengerConnector.status()
            .throttleLast(50, TimeUnit.MILLISECONDS)
            .compose(bindViewIoToMainComposer());

      connectionStatusStream.subscribe(connectionStatus -> {
         currentConnectivityStatus = connectionStatus;
         if (isViewAttached()) {
            getView().onConnectionChanged(connectionStatus);
         }
      });
   }

   protected boolean isConnectionPresent() {
      return currentConnectivityStatus == SyncStatus.CONNECTED;
   }

   protected void showAbsentConnectionMessage(Context context) {
      Toast.makeText(context, R.string.warning_no_internet_connection, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onDisconnectedOverlayClicked() {
      messengerConnector.connect();
   }

   protected Context getContext() {
      return context;
   }

   protected Injector getInjector() {
      return injector;
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
}
