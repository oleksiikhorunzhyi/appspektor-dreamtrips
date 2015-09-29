package com.worldventures.dreamtrips.modules.gcm.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDataParser;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.model.PushType;

import javax.inject.Inject;

import timber.log.Timber;

public class PushListenerService extends GcmListenerService {

    @Inject
    NotificationDelegate delegate;
    @Inject
    NotificationDataParser parser;

    @Override
    public void onCreate() {
        super.onCreate();
        ((BaseApplicationWithInjector) getApplication()).inject(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Timber.i("Push message received: " + data);
        //
        PushType type = parser.obtainPushType(data);
        switch (type) {
            case ACCEPT_REQUEST:
                delegate.notifyFriendRequestAccepted(data);
                break;
            case SEND_REQUEST:
                delegate.notifyFriendRequestReceived(data);
                break;
            default:
                Timber.w("Unknown message type: %s", data.getString("type"));
                break;
        }
        //
        delegate.updateNotificationCount(data);
    }

}
