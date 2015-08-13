package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import java.util.ArrayList;

public class GetRequestsQuery extends Query<ArrayList<Friend>> {

    public GetRequestsQuery() {
        super((Class<ArrayList<Friend>>) new ArrayList<Friend>().getClass());
    }

    @Override
    public ArrayList<Friend> loadDataFromNetwork() throws Exception {
        return getService().getRequests();
    }
}
