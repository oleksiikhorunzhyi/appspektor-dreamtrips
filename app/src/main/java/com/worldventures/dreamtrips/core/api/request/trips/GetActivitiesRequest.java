package com.worldventures.dreamtrips.core.api.request.trips;

import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.ArrayList;

public class GetActivitiesRequest extends DreamTripsRequest<ArrayList<Activity>> {

    private SnappyRepository db;

    public GetActivitiesRequest(SnappyRepository snappyRepository) {
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
