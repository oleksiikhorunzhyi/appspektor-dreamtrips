package com.worldventures.dreamtrips.modules.gcm.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

public class PushListenerService extends GcmListenerService {

    private NotificationDelegate notificationDelegate;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationDelegate = new NotificationDelegate(this);

        ((BaseApplicationWithInjector) getApplication()).inject(this);
        ((BaseApplicationWithInjector) getApplication()).inject(notificationDelegate);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        PushType type = PushType.forType(data.getString("type"));

        //based on type we decide how to show push notification
        switch (type) {
            case ACCEPT_REQUEST:
            case SEND_REQUEST:
                //TODO implement message construction base on actual implementation
                notificationDelegate.sendFriendNotification("",
                        data.getInt("user_id", -1), data.getInt("notification_id", -1));

                break;
        }
    }

    public enum PushType {
        ACCEPT_REQUEST, SEND_REQUEST;

        public static PushType forType(String type) {
            return Queryable.from(values()).firstOrDefault(element ->
                    element.name().equalsIgnoreCase(type));
        }
    }

}
