package com.worldventures.dreamtrips.modules.friends.api;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

public class SearchUsersQuery extends Query<ArrayList<User>> {

    private String query;
    private int page;
    private int perPage;

    public SearchUsersQuery(String query, int page, int perPage) {
        super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
        this.query = query;
        this.page = page;
        this.perPage = perPage;
    }

    @Override
    public ArrayList<User> loadDataFromNetwork() throws Exception {
        if (TextUtils.isEmpty(query) || query.length() < 3) return new ArrayList<>();
        return getService().searchUsers(query, page, perPage);
    }
}
