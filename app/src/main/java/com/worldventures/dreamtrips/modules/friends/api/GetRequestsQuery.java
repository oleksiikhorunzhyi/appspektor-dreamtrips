package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

public class GetRequestsQuery extends Query<ArrayList<User>> {

    public GetRequestsQuery() {
        super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
    }

    @Override
    public ArrayList<User> loadDataFromNetwork() throws Exception {
        return getService().getRequests();
    }
}