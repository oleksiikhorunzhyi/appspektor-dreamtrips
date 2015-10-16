package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;

public class UnsubscribeDeviceCommand extends Command<Void> {

    private String token;

    public UnsubscribeDeviceCommand(String token) {
        super(Void.class);
        this.token = token;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().unsubscribeDevice(token);
    }
}
