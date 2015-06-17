package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.Request;

import java.util.ArrayList;

public class GetRequestsQuery extends Query<ArrayList<Request>> {

    public GetRequestsQuery() {
        super((Class<ArrayList<Request>>) new ArrayList<Request>().getClass());
    }

    @Override
    public ArrayList<Request> loadDataFromNetwork() throws Exception {
        return getService().getRequests();
    }
}
