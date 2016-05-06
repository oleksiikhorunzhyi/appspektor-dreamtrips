package com.messenger.ui.view.layout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.presenter.MessengerPresenter;
import com.worldventures.dreamtrips.R;

import icepick.Icepick;

public abstract class MessengerLinearLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>>
        extends BaseViewStateLinearLayout<V, P> implements MessengerScreen {

    private OverlayHandler overlayHandler = new OverlayHandler();

    public MessengerLinearLayout(Context context) {
        super(context);
    }

    public MessengerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Bundle lastRestoredInstanceState;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        overlayHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onConnectionChanged(SyncStatus syncStatus) {
        overlayHandler.processOverlayConnectionStatus(syncStatus);
    }

    private class OverlayHandler extends Handler {

        private static final int SHOW_CONNECTING_VIEW_MIN_DURATION = 1000;

        private View overlay;
        private View disconnectedView;
        private View connectingView;

        private SyncStatus lastSyncStatus;
        private boolean delayedStatusPending;

        private void attachDisconnectedOverlayIfNeeded() {
            if (overlay != null) {
                return;
            }
            overlay = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_disconnected_overlay, getContentView(), false);
            overlay.findViewById(R.id.messenger_reconnect_button)
                    .setOnClickListener(v -> getPresenter().onDisconnectedOverlayClicked());
            disconnectedView = overlay.findViewById(R.id.messenger_connection_overlay_disconnected_view);
            connectingView = overlay.findViewById(R.id.messenger_connection_overlay_connecting_view);
            getContentView().addView(overlay);
            overlay.setVisibility(GONE);
        }

        @Override
        public void handleMessage(Message msg) {
            attachDisconnectedOverlayIfNeeded();
            SyncStatus status = SyncStatus.values()[msg.what];
            switch (status) {
                case CONNECTED:
                    overlay.setVisibility(GONE);
                    break;
                case CONNECTING:
                    overlay.setVisibility(VISIBLE);
                    connectingView.setVisibility(VISIBLE);
                    disconnectedView.setVisibility(GONE);
                    break;
                case ERROR:
                case DISCONNECTED:
                default:
                    overlay.setVisibility(VISIBLE);
                    disconnectedView.setVisibility(VISIBLE);
                    connectingView.setVisibility(GONE);
                    break;
            }
            // delay to message set as arg1
            if (msg.arg1 > 0) {
                delayedStatusPending = false;
                processOverlayConnectionStatus(lastSyncStatus);
            }
        }

        public void processOverlayConnectionStatus(SyncStatus syncStatus) {
            // apply delayed state first, then the last state
            if (!delayedStatusPending) {
                int delay = 0;
                if (lastSyncStatus != null
                        && lastSyncStatus == SyncStatus.CONNECTING
                        && syncStatus != SyncStatus.CONNECTING) {
                    delay = SHOW_CONNECTING_VIEW_MIN_DURATION;
                    delayedStatusPending = true;
                }
                Message message = new Message();
                message.what = syncStatus.ordinal();
                message.arg1 = delay;
                sendMessageDelayed(message, delay);
            }
            this.lastSyncStatus = syncStatus;
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

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        lastRestoredInstanceState = (Bundle) state;
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    public Bundle getLastRestoredInstanceState() {
        return lastRestoredInstanceState;
    }

    protected abstract ViewGroup getContentView();
}
