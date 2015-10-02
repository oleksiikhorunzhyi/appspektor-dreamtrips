package com.worldventures.dreamtrips.modules.feed.api;

import android.os.Build;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.notification.PushSubscription;

public class SubscribeDeviceCommand extends Command<Void> {

    private String token;

    public SubscribeDeviceCommand(String token) {
        super(Void.class);
        this.token = token;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().subscribeDevice(new PushSubscription(token,
                "android", BuildConfig.VERSION_NAME, String.valueOf(Build.VERSION.SDK_INT)));
    }
}
