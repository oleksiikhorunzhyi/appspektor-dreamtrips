package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;

public class MarkNotificationAsReadCommand extends Command<Void> {

    private int id;

    public MarkNotificationAsReadCommand(int id) {
        super(Void.class);
        this.id = id;

    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().markAsRead(id);
    }
}