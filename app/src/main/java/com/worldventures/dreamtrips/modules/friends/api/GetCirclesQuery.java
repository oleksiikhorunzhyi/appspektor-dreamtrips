package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;

public class GetCirclesQuery extends Query<ArrayList<Circle>> {

    private final SnappyRepository repository;

    public GetCirclesQuery(SnappyRepository snappyRepository) {
        super((Class<ArrayList<Circle>>) new ArrayList<Circle>().getClass());
        repository = snappyRepository;
    }

    @Override
    public ArrayList<Circle> loadDataFromNetwork() throws Exception {
        ArrayList<Circle> circles = getService().getCircles();
        repository.saveCircles(circles);
        return circles;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_circles;
    }

}