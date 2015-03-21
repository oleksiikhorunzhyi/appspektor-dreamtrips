package com.worldventures.dreamtrips.core.api.request.trips;

import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.ArrayList;

public class GetRegionsRequest extends DreamTripsRequest<ArrayList<Region>> {

    private SnappyRepository db;

    public GetRegionsRequest(SnappyRepository snappyRepository) {
        super((Class<ArrayList<Region>>) new ArrayList<Region>().getClass());
        this.db = snappyRepository;
    }

    @Override
    public ArrayList<Region> loadDataFromNetwork() throws Exception {
        ArrayList<Region> data = new ArrayList<>();
        if (db.isEmpty(SnappyRepository.REGIONS)) {
            data.addAll(getService().getRegions());
            db.putList(data, SnappyRepository.REGIONS, Region.class);

        } else {
            data.addAll(db.readList(SnappyRepository.REGIONS, Region.class));
        }
        return data;
    }
}
