package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.ArrayList;

public class GetRegionsQuery extends Query<ArrayList<RegionModel>> {

    private SnappyRepository db;
    private boolean fromApi;


    public GetRegionsQuery(SnappyRepository snappyRepository, boolean fromApi) {
        super((Class<ArrayList<RegionModel>>) new ArrayList<RegionModel>().getClass());
        this.db = snappyRepository;
        this.fromApi = fromApi;
    }

    @Override
    public ArrayList<RegionModel> loadDataFromNetwork() {
        ArrayList<RegionModel> data = new ArrayList<>();
        if (db.isEmpty(SnappyRepository.REGIONS) || fromApi) {
            data.addAll(getService().getRegions());
            db.putList(SnappyRepository.REGIONS, data);
        } else {
            data.addAll(db.readList(SnappyRepository.REGIONS, RegionModel.class));
        }
        return data;
    }
}
