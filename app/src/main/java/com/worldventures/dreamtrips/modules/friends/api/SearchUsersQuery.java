package com.worldventures.dreamtrips.modules.friends.api;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

public class SearchUsersQuery extends Query<ArrayList<User>> {

    private String query;
    private int offset;
    private int limit;

    public SearchUsersQuery(Class<ArrayList<User>> clazz, String query) {
        super(clazz);
        this.query = query;
    }

    public SearchUsersQuery(String query, int offset, int limit) {
        super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
        this.query = query;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public ArrayList<User> loadDataFromNetwork() throws Exception {
        if (TextUtils.isEmpty(query) || query.length() < 3) return new ArrayList<>();
        return getService().searchUsers(query, offset, limit);
    }
}
