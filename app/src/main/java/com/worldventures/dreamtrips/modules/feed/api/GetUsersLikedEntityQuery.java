package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

public class GetUsersLikedEntityQuery extends Query<ArrayList<User>> {

    String uid;
    int page;
    int perPage;

    public GetUsersLikedEntityQuery(String uid, int page, int perPage) {
        super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
        this.uid = uid;
        this.page = page;
        this.perPage = perPage;
    }

    @Override
    public ArrayList<User> loadDataFromNetwork() throws Exception {
        return getService().getUsersWhoLikedEntity(uid, page, perPage);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_people_who_liked;
    }
}
