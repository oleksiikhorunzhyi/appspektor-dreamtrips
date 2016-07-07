package com.worldventures.dreamtrips.modules.feed.api;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;
import java.util.Date;

public class GetAccountFeedQuery extends Query<ArrayList<ParentFeedItem>> {

    public static final int LIMIT = 1;
    private Date before;
    private String circleId;

    public GetAccountFeedQuery(String id) {
        this(null, id);
    }

    public GetAccountFeedQuery(Date before, @Nullable String circleId) {
        super((Class<ArrayList<ParentFeedItem>>) new ArrayList<ParentFeedItem>().getClass());
        this.before = before;
        this.circleId = circleId;
    }

    @Override
    public ArrayList<ParentFeedItem> loadDataFromNetwork() throws Exception {
        ArrayList<ParentFeedItem> result;
        String before = this.before == null ? null : DateTimeUtils.convertDateToUTCString(this.before);
        result = getService().getAccountFeed(LIMIT, before, circleId);
        return result;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_feed;
    }
}
