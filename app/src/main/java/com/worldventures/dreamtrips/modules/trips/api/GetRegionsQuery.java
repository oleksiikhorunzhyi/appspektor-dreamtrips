package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.ArrayList;

public class GetRegionsQuery extends Query<ArrayList<RegionModel>> {

    private SnappyRepository db;

    public GetRegionsQuery(SnappyRepository snappyRepository) {
        super((Class<ArrayList<RegionModel>>) new ArrayList<RegionModel>().getClass());
        this.db = snappyRepository;
    }

    @Override
    public ArrayList<RegionModel> loadDataFromNetwork() {
        ArrayList<RegionModel> data = new ArrayList<>();
        if (db.isEmpty(SnappyRepository.REGIONS)) {
            data.addAll(getService().getRegions());
            db.putList(data, SnappyRepository.REGIONS);
        } else {
            data.addAll(db.readList(SnappyRepository.REGIONS, RegionModel.class));
        }
        return data;
    }
}
