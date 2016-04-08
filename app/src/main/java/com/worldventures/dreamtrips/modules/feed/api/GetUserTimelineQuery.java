package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;
import java.util.Date;

public class GetUserTimelineQuery extends Query<ArrayList<ParentFeedItem>> {

    public static final int LIMIT = 10;
    private int userId;
    private Date before;

    public GetUserTimelineQuery(int id) {
        this(id, null);
    }

    public GetUserTimelineQuery(int userId, Date before) {
        super((Class<ArrayList<ParentFeedItem>>) new ArrayList<ParentFeedItem>().getClass());
        this.userId = userId;
        this.before = before;
    }

    @Override
    public ArrayList<ParentFeedItem> loadDataFromNetwork() throws Exception {
        String before = this.before == null ? null : DateTimeUtils.convertDateToUTCString(this.before);
        return getService().getUserTimeline(userId, LIMIT, before);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_timeline;
    }
}
