package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;
import java.util.Date;

public class GetAccountTimelineQuery extends Query<ArrayList<ParentFeedItem>> {

    public static final int LIMIT = 10;
    private Date before;

    public GetAccountTimelineQuery(Date before) {
        super((Class<ArrayList<ParentFeedItem>>) new ArrayList<ParentFeedItem>().getClass());
        this.before = before;
    }

    @Override
    public ArrayList<ParentFeedItem> loadDataFromNetwork() throws Exception {
        return getService().getAccountTimeline(LIMIT, DateTimeUtils.convertDateToUTCString(before));
    }


    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_timeline;
    }
}

