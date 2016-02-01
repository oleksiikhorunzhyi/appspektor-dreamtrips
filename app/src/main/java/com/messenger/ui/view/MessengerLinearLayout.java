package com.messenger.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.synchmechanism.ConnectionStatus;
import com.messenger.ui.presenter.MessengerPresenter;
import com.worldventures.dreamtrips.R;

public abstract class MessengerLinearLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>>
        extends BaseViewStateLinearLayout<V, P> implements MessengerScreen {

    private OverlayHandler overlayHandler = new OverlayHandler();

    public MessengerLinearLayout(Context context) {
        super(context);
    }

    public MessengerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        overlayHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStart() {
        if (getPresenter() != null) getPresenter().onStart();
    }

    @Override
    public void onStop() {
        getPresenter().onStop();
    }

    @Override
    public void onDestroy() {
        getPresenter().onDestroy();
    }

    @Override
    public void onConnectionChanged(ConnectionStatus connectionStatus) {
        overlayHandler.processOverlayConnectionStatus(connectionStatus);
    }

    private class OverlayHandler extends Handler {

        private static final int SHOW_CONNECTING_VIEW_MIN_DURATION = 1000;

        private View overlay;
        private View disconnectedView;
        private View connectingView;

        private ConnectionStatus lastConnectionStatus;
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
            ConnectionStatus status = ConnectionStatus.values()[msg.what];
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
                processOverlayConnectionStatus(lastConnectionStatus);
            }
        }

        public void processOverlayConnectionStatus(ConnectionStatus connectionStatus) {
            // apply delayed state first, then the last state
            if (!delayedStatusPending) {
                int delay = 0;
                if (lastConnectionStatus != null
                        && lastConnectionStatus == ConnectionStatus.CONNECTING
                        && connectionStatus != ConnectionStatus.CONNECTING) {
                    delay = SHOW_CONNECTING_VIEW_MIN_DURATION;
                    delayedStatusPending = true;
                }
                Message message = new Message();
                message.what = connectionStatus.ordinal();
                message.arg1 = delay;
                sendMessageDelayed(message, delay);
            }
            this.lastConnectionStatus = connectionStatus;
        }
    }

    protected abstract ViewGroup getContentView();
}
