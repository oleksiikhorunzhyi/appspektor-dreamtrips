package com.messenger.ui.presenter;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;

import com.messenger.synchmechanism.ConnectionStatus;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.ui.view.MessengerScreen;


import rx.Subscription;
import timber.log.Timber;

public abstract class MessengerPresenterImpl<V extends MessengerScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements MessengerPresenter<V, S> {

    private static final int DISCONNECTED_OVERLAY_HIDE_DELAY = 3000;

    private OverlayHandler handler = new OverlayHandler(Looper.getMainLooper());

    private Subscription connectedSubscription;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        connectedSubscription = MessengerConnector.getInstance().subscribe()
                .subscribe(connectionStatus -> {
                    Timber.d("Connection " + connectionStatus);
                    if (connectionStatus == ConnectionStatus.CONNECTED ||
                            connectionStatus == ConnectionStatus.CONNECTING) {
                        handler.hideOverlay();
                    } else {
                        handler.showOverlay();
                    }
                });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
        connectedSubscription.unsubscribe();
    }

    @Override
    public void onDisconnectedOverlayClicked() {
        MessengerConnector.getInstance().connect();
    }

    private class OverlayHandler extends Handler {

        private static final int SHOW = 1;
        private static final int HIDE = 2;

        public OverlayHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (!isViewAttached()) {
                return;
            }
            switch (msg.what) {
                case SHOW:
                    getView().showDisconnectedOverlay();
                    break;
                case HIDE:
                    getView().hideDisconnectedOverlay();
                    break;
            }
        }

        public void showOverlay() {
            removeCallbacksAndMessages(null);
            sendEmptyMessage(SHOW);
        }

        public void hideOverlay() {
            if (!hasMessages(HIDE)) {
                sendEmptyMessageDelayed(HIDE, DISCONNECTED_OVERLAY_HIDE_DELAY);
            }
        }
    }
}
