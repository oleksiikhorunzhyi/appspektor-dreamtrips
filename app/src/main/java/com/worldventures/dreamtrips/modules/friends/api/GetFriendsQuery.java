package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.UserWrapper;

import java.util.ArrayList;

public class GetFriendsQuery extends Query<ArrayList<UserWrapper>> {

    private String group;
    private int offset;
    private int limit;

    public GetFriendsQuery() {
        this("all", 0, 10);
    }

    public GetFriendsQuery(String group, int offset, int limit) {
        super((Class<ArrayList<UserWrapper>>) new ArrayList<UserWrapper>().getClass());
        this.group = group;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public ArrayList<UserWrapper> loadDataFromNetwork() throws Exception {
        return getService().getFriends(group, offset, limit);
    }
}
