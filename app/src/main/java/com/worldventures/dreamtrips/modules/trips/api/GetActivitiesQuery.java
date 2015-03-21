package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.Activity;

import java.util.ArrayList;

public class GetActivitiesQuery extends Query<ArrayList<Activity>> {

    private SnappyRepository db;

    public GetActivitiesQuery(SnappyRepository snappyRepository) {
        super((Class<ArrayList<Activity>>) new ArrayList<Activity>().getClass());
        this.db = snappyRepository;
    }

    @Override
    public ArrayList<Activity> loadDataFromNetwork() throws Exception {
        ArrayList<Activity> data = new ArrayList<>();
        if (db.isEmpty(SnappyRepository.ACTIVITIES)) {
            data.addAll(getService().getActivities());
            db.putList(data, SnappyRepository.ACTIVITIES, Activity.class);
        } else {
            data.addAll(db.readList(SnappyRepository.ACTIVITIES, Activity.class));
        }
        return data;
    }
}
