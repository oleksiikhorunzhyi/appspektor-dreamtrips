package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

public class UnlikeEntityCommand extends DreamTripsRequest<Void> {

    private String uid;

    public UnlikeEntityCommand(String uid) {
        super(Void.class);
        this.uid = uid;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().dislikeEntity(uid);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_unlike_item;
    }
}
