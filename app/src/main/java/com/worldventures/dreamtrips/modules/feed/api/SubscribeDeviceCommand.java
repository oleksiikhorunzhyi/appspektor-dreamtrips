package com.worldventures.dreamtrips.modules.feed.api;

import android.os.Build;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.notification.PushSubscription;

public class SubscribeDeviceCommand extends Command<Void> {

    private String token;
    private String releaseSemanticVersionName;

    public SubscribeDeviceCommand(String token, String releaseSemanticVersionName) {
        super(Void.class);
        this.token = token;
        this.releaseSemanticVersionName = releaseSemanticVersionName;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().subscribeDevice(new PushSubscription(token,
                "android", releaseSemanticVersionName, String.valueOf(Build.VERSION.SDK_INT)));
    }
}
