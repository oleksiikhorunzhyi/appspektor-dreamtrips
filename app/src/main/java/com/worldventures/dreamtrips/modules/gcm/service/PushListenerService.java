package com.worldventures.dreamtrips.modules.gcm.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import javax.inject.Inject;

public class PushListenerService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 424242;

    private NotificationDelegate notificationDelegate;

    @Inject
    Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationDelegate = new NotificationDelegate(this);

        ((BaseApplicationWithInjector) getApplication()).inject(this);
        ((BaseApplicationWithInjector) getApplication()).inject(notificationDelegate);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        processNotification(data.getString("payload"));
    }

    private void processNotification(String json) {
        FeedUndefinedEventModel eventModel = gson.fromJson(json, FeedUndefinedEventModel.class);
        if (eventModel != null)
            notificationDelegate.sendFriendNotification(eventModel);
    }

}
