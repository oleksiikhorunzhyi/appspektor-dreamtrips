package com.messenger.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.service.InjectingService;

import javax.inject.Inject;

public class MessengerNotificationPreSyncService extends InjectingService {
    @Inject MessengerConnector messengerConnector;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        messengerConnector.connectAfterGlobalConfig();
    }
}
