package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

import java.util.ArrayList;

public class GetUserFeedQuery extends Query<ArrayList<BaseFeedModel>> {

    public static final int LIMIT = 10;
    private int userId;
    private int offset;

    public GetUserFeedQuery(int userId, int offset) {
        super((Class<ArrayList<BaseFeedModel>>) new ArrayList<BaseFeedModel>().getClass());
        this.userId = userId;
        this.offset = offset;
    }


    @Override
    public ArrayList<BaseFeedModel> loadDataFromNetwork() throws Exception {
        return getService().getUserFeed(userId, LIMIT, offset + 1);
    }
}
