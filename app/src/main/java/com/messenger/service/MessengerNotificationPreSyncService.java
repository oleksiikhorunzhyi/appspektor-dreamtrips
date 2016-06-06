package com.messenger.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.service.InjectingService;

public class MessengerNotificationPreSyncService extends InjectingService {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MessengerConnector.getInstance().connectAfterGlobalConfig();
    }
}
