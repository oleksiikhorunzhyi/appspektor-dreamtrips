package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

import java.util.ArrayList;

public class GetFeedQuery extends Query<ArrayList<BaseFeedModel>> {

    public static final int LIMIT = 10;
    private int offset;

    public GetFeedQuery(int offset) {
        super((Class<ArrayList<BaseFeedModel>>) new ArrayList<BaseFeedModel>().getClass());
        this.offset = offset;
    }

    @Override
    public ArrayList<BaseFeedModel> loadDataFromNetwork() throws Exception {
        return getService().getFeedActivity(LIMIT, offset);
    }
}
