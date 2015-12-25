package com.messenger.ui.inappnotifications;

import android.app.Activity;

import com.messenger.ui.widget.inappnotification.BaseInAppNotificationView;
import com.messenger.ui.widget.inappnotification.InAppNotificationViewListener;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.worldventures.dreamtrips.App;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class AppNotificationImpl implements AppNotification {

    private Activity activity;
    private Crouton crouton;

    public AppNotificationImpl(App app) {
        app.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks(){
            @Override
            public void onActivityStopped(Activity activity) {
                AppNotificationImpl.this.activity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                AppNotificationImpl.this.activity = null;
            }
        });
    }

    @Override
    public void show (BaseInAppNotificationView view, final InAppNotificationEventListener listener){
        view.setListener(new InAppNotificationViewListener() {
            @Override
            public void onClick() {
                crouton.hide();
                if (listener != null){
                    listener.onClick();
                }
            }

            @Override
            public void onCloseClick() {
                crouton.hide();
                if (listener != null){
                    listener.onClose();
                }

            }

            @Override
            public void onCloseSwipe() {
                crouton.hide();
                if (listener != null){
                    listener.onClose();
                }

            }
        });
        crouton = Crouton.make(activity, view);
        crouton.setConfiguration(new Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE)
                .setOutAnimation(0)
                .build()
        );
        crouton.show();
    };

    @Override
    public void dismissForActivity(Activity activity){
        Crouton.clearCroutonsForActivity(activity);
    }
}
