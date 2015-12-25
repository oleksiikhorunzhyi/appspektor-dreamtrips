package com.messenger.ui.inappnotifications;

import android.app.Activity;

import com.messenger.ui.widget.inappnotification.BaseInAppNotificationView;
import com.messenger.ui.widget.inappnotification.InAppNotificationViewListener;
import com.worldventures.dreamtrips.App;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class AppNotificationImpl implements AppNotification {

    private static final int HIDE_DELAY = 1000;
    private Crouton crouton;

    public AppNotificationImpl(App app) {
    }

    @Override
    public void show(Activity activity, BaseInAppNotificationView view, final InAppNotificationEventListener listener) {
        view.setListener(new InAppNotificationViewListener() {
            @Override
            public void onClick() {
                crouton.hide();
                if (listener != null) {
                    listener.onClick();
                }
            }

            @Override
            public void onCloseClick() {
                crouton.hide();
                if (listener != null) {
                    listener.onClose();
                }
            }

            @Override
            public void onCloseSwipe() {
                crouton.hide();
                if (listener != null) {
                    listener.onClose();
                }
            }
        });

        crouton = Crouton.make(activity, view);
        crouton.setConfiguration(new Configuration.Builder()
                        .setDuration(HIDE_DELAY)
                        .setOutAnimation(0)
                        .build()
        );
        crouton.show();
    }

    @Override
    public void dismissForActivity(Activity activity) {
        Crouton.clearCroutonsForActivity(activity);
    }
}
