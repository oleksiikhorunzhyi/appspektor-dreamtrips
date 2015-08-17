package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

import java.util.ArrayList;

public class GetFeedQuery extends Query<ArrayList<ParentFeedModel>> {

    public static final int LIMIT = 10;
    private int offset;

    public GetFeedQuery(int offset) {
        super((Class<ArrayList<ParentFeedModel>>) new ArrayList<ParentFeedModel>().getClass());
        this.offset = offset;
    }

    @Override
    public ArrayList<ParentFeedModel> loadDataFromNetwork() throws Exception {
        return getService().getUserFeed(LIMIT, offset + 1);
    }
}
