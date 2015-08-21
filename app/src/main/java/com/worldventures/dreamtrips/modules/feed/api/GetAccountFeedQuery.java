package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

import java.util.ArrayList;
import java.util.Date;

public class GetAccountFeedQuery extends Query<ArrayList<ParentFeedModel>> {

    public static final int LIMIT = 10;
    private Date before;

    public GetAccountFeedQuery(Date before) {
        super((Class<ArrayList<ParentFeedModel>>) new ArrayList<ParentFeedModel>().getClass());
        this.before = before;
    }

    @Override
    public ArrayList<ParentFeedModel> loadDataFromNetwork() throws Exception {
        return getService().getAccountFeed(LIMIT, DateTimeUtils.convertDateToUTCString(before));
    }
}
