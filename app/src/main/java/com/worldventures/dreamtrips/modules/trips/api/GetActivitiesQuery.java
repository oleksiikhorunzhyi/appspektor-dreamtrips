package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import java.util.ArrayList;

public class GetActivitiesQuery extends Query<ArrayList<ActivityModel>> {

    private SnappyRepository db;

    public GetActivitiesQuery(SnappyRepository snappyRepository) {
        super((Class<ArrayList<ActivityModel>>) new ArrayList<ActivityModel>().getClass());
        this.db = snappyRepository;
    }

    @Override
    public ArrayList<ActivityModel> loadDataFromNetwork() throws Exception {
        ArrayList<ActivityModel> data = new ArrayList<>();
        if (db.isEmpty(SnappyRepository.ACTIVITIES)) {
            data.addAll(getService().getActivities());
            db.putList(data, SnappyRepository.ACTIVITIES, ActivityModel.class);
        } else {
            data.addAll(db.readList(SnappyRepository.ACTIVITIES, ActivityModel.class));
        }
        return data;
    }
}
