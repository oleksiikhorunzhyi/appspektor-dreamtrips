package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

public class UnlikeEntityCommand extends DreamTripsRequest<Void> {

    private long uid;

    public UnlikeEntityCommand(long uid) {
        super(Void.class);
        this.uid = uid;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().dislikeEntity(uid);
    }
}
