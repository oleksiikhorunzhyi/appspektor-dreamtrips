package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import java.util.ArrayList;

public class GetFriendsQuery extends Query<ArrayList<Friend>> {

    private String group;
    private int offset;
    private int limit;

    public GetFriendsQuery(String type) {
        this(type, 0, 10);
    }

    public GetFriendsQuery(String group, int offset, int limit) {
        super((Class<ArrayList<Friend>>) new ArrayList<Friend>().getClass());
        this.group = group;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public ArrayList<Friend> loadDataFromNetwork() throws Exception {
        return getService().getFriends(group, offset, limit);
    }
}
