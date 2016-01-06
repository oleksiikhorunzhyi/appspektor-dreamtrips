package com.messenger.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.ui.presenter.MessengerPresenter;
import com.worldventures.dreamtrips.R;

import timber.log.Timber;

public abstract class MessengerLinearLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>>
        extends BaseViewStateLinearLayout<V, P> implements MessengerScreen {

    private View disconnectedOverlay;

    public MessengerLinearLayout(Context context) {
        super(context);
    }

    public MessengerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void attachDisconnectedOverlay() {
        disconnectedOverlay = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_disconnected_overlay, getContentView(), false);
        getContentView().addView(disconnectedOverlay);
        disconnectedOverlay.findViewById(R.id.messenger_reconnect_button)
                .setOnClickListener(v -> getPresenter().onDisconnectedOverlayClicked());
        hideDisconnectedOverlay();
    }

    @Override
    public void showDisconnectedOverlay() {
        if (disconnectedOverlay == null) {
            attachDisconnectedOverlay();
        }
        disconnectedOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDisconnectedOverlay() {
        if (disconnectedOverlay != null) {
            disconnectedOverlay.setVisibility(View.GONE);
        }
    }

    protected abstract ViewGroup getContentView();
}
