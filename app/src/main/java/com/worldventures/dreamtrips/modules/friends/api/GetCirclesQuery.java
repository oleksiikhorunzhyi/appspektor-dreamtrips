package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;

public class GetCirclesQuery extends Query<ArrayList<Circle>> {

    public GetCirclesQuery() {
        super((Class<ArrayList<Circle>>) new ArrayList<Circle>().getClass());
    }

    @Override
    public ArrayList<Circle> loadDataFromNetwork() throws Exception {
        return getService().getCircles();
    }
}