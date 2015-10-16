package com.worldventures.dreamtrips.modules.friends.api;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

public class SearchUsersQuery extends Query<ArrayList<User>> {

    public static final int PER_PAGE = 20;

    private String query;
    private int offset;

    public SearchUsersQuery(String query, int offset) {
        super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
        this.query = query;
        this.offset = offset;
    }

    @Override
    public ArrayList<User> loadDataFromNetwork() throws Exception {
        if (TextUtils.isEmpty(query) || query.length() < 3) return new ArrayList<>();
        return getService().searchUsers(query, offset, PER_PAGE);
    }
}
