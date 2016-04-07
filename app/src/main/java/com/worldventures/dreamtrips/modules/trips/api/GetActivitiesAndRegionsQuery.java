package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

public class GetActivitiesAndRegionsQuery extends Query<Void> {

    private SnappyRepository db;

    public GetActivitiesAndRegionsQuery(SnappyRepository snappyRepository) {
        super(Void.class);
        this.db = snappyRepository;
    }

    @Override
    public Void loadDataFromNetwork() {
        db.putList(SnappyRepository.ACTIVITIES, getService().getActivities());
        db.putList(SnappyRepository.REGIONS, getService().getRegions());
        return null;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_activities;
    }
}
