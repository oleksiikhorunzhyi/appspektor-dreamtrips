package com.worldventures.dreamtrips.modules.gcm.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import javax.inject.Inject;

public class PushListenerService extends GcmListenerService {

    @Inject
    Gson gson;

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
        processNotification(data.getString("payload"), data.getInt("notification_id", -1));
    }

    private void processNotification(String json, int notificationId) {
        FeedUndefinedEventModel eventModel = gson.fromJson(json, FeedUndefinedEventModel.class);
        if (eventModel != null && eventModel.getAction() != null) {
            if (eventModel.getAction().equals(BaseEventModel.Action.ACCEPT_REQUEST) ||
                    eventModel.getAction().equals(BaseEventModel.Action.SEND_REQUEST))
                notificationDelegate.sendFriendNotification(eventModel, notificationId);
            else {
                //TODO
            }
        }
    }

}
