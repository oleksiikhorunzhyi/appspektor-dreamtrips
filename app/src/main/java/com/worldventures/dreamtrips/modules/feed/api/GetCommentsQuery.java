package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.ArrayList;

public class GetCommentsQuery extends Query<ArrayList<Comment>> {

    public static final int LIMIT = 10;
    private int page;
    private int feedItemId;

    public GetCommentsQuery(int feedItemId, int page) {
        super((Class<ArrayList<Comment>>) new ArrayList<Comment>().getClass());
        this.feedItemId = feedItemId;
        this.page = page;
    }

    @Override
    public ArrayList<Comment> loadDataFromNetwork() throws Exception {
        return getService().getComments(feedItemId, LIMIT, page);
    }
}
