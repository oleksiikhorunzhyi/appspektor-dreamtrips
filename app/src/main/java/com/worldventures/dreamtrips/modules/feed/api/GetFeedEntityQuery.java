package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;

public class GetFeedEntityQuery extends Query<FeedEntityHolder> {

    private String uid;

    public GetFeedEntityQuery(String uid) {
        super(FeedEntityHolder.class);
        this.uid = uid;
    }

    @Override
    public FeedEntityHolder loadDataFromNetwork() throws Exception {
        return getService().getFeedEntity(uid);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_item_details;
    }
}
