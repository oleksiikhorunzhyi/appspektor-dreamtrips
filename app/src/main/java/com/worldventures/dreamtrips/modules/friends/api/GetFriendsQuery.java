package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import java.util.ArrayList;

public class GetFriendsQuery extends Query<ArrayList<Friend>> {

    private Circle circle;
    private String query;
    private int offset;

    public GetFriendsQuery(Circle circle, String query, int offset) {
        super((Class<ArrayList<Friend>>) new ArrayList<Friend>().getClass());
        this.circle = circle;
        this.query = query != null && query.length() > 2
                ? query
                : null;
        this.offset = offset;
    }

    @Override
    public ArrayList<Friend> loadDataFromNetwork() throws Exception {
        if (circle != null)
            return getService().getFriends(circle.getId(), query, offset);
        else
            return getService().getAllFriends(query, offset);
    }
}
