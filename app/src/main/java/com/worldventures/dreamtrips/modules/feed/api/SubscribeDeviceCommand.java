package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.notification.PushSubscription;

public class SubscribeDeviceCommand extends Command<Void>{

    private PushSubscription pushSubscription;

    public SubscribeDeviceCommand(PushSubscription pushSubscription) {
        super(Void.class);
        this.pushSubscription = pushSubscription;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().subscribeDevice(pushSubscription);
    }
}
