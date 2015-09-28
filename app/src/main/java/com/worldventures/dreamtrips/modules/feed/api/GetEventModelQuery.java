package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObjectHolder;

public class GetEventModelQuery extends Query<IFeedObjectHolder> {

    private String uid;

    public GetEventModelQuery(String uid) {
        super(IFeedObjectHolder.class);
        this.uid = uid;
    }

    @Override
    public IFeedObjectHolder loadDataFromNetwork() throws Exception {
        return getService().getEventModel(uid);
    }
}
