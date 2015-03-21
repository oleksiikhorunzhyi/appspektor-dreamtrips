package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.Region;

import java.util.ArrayList;

public class GetRegionsQuery extends Query<ArrayList<Region>> {

    private SnappyRepository db;

    public GetRegionsQuery(SnappyRepository snappyRepository) {
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
