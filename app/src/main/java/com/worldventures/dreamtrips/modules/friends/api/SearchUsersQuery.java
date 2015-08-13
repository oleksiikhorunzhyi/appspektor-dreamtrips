package com.worldventures.dreamtrips.modules.friends.api;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import java.util.ArrayList;

public class SearchUsersQuery extends Query<ArrayList<Friend>> {

    private String query;
    private int offset;
    private int limit;

    public SearchUsersQuery(String query, int offset, int limit) {
        super((Class<ArrayList<Friend>>) new ArrayList<User>().getClass());
        this.query = query;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public ArrayList<Friend> loadDataFromNetwork() throws Exception {
        if (TextUtils.isEmpty(query) || query.length() < 3) return new ArrayList<>();
        return getService().searchUsers(query, offset, limit);
    }
}
