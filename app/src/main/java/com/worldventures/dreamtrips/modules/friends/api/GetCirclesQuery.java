package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;

public class GetCirclesQuery extends Query<ArrayList<Circle>> {

    private int userId;

    public GetCirclesQuery(int userId) {
        super((Class<ArrayList<Circle>>) new ArrayList<Circle>().getClass());
        this.userId = userId;
    }

    @Override
    public ArrayList<Circle> loadDataFromNetwork() throws Exception {
        return getService().getCircles(userId);
    }
}