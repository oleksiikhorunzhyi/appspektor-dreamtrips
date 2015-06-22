package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import java.util.ArrayList;

public class GetFriendsQuery extends Query<ArrayList<Friend>> {

    private Circle circle;
    private int offset;
    private int limit;

    public GetFriendsQuery(Circle type) {
        this(type, 0, 10);
    }

    public GetFriendsQuery(Circle circle, int offset, int limit) {
        super((Class<ArrayList<Friend>>) new ArrayList<Friend>().getClass());
        this.circle = circle;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public ArrayList<Friend> loadDataFromNetwork() throws Exception {
        if (circle != null)
            return getService().getFriends(circle.getId(), offset, limit);
        else
            return getService().getAllFriends(offset, limit);
    }
}
