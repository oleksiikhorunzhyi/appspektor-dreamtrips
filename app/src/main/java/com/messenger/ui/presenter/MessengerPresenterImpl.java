package com.messenger.ui.presenter;


import android.content.Context;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.synchmechanism.ConnectionStatus;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.ui.view.MessengerScreen;
import com.worldventures.dreamtrips.R;

import java.util.concurrent.TimeUnit;

public abstract class MessengerPresenterImpl<V extends MessengerScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements MessengerPresenter<V, S> {

    private Context context;

    protected ConnectionStatus currentConnectivityStatus = ConnectionStatus.DISCONNECTED;

    public MessengerPresenterImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MessengerConnector.getInstance().status()
                .compose(bindView())
                .throttleLast(50, TimeUnit.MILLISECONDS)
                .subscribe(connectionStatus -> {
                    currentConnectivityStatus = connectionStatus;
                    if (isViewAttached()) {
                        getView().onConnectionChanged(connectionStatus);
                    }
                });
    }

    protected boolean isConnectionPresent(){
        return currentConnectivityStatus == ConnectionStatus.CONNECTED;
    }

    protected void showAbsentConnectionMessage(Context context){
        Toast.makeText(context, R.string.warning_no_internet_connection,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnectedOverlayClicked() {
        MessengerConnector.getInstance().connect();
    }

    protected Context getContext() {
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
}
