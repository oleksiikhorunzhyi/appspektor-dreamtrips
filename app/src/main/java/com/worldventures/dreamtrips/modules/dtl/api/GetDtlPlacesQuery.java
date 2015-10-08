package com.worldventures.dreamtrips.modules.dtl.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

import java.util.ArrayList;

public class GetDtlPlacesQuery extends Query<ArrayList<DtlPlace>> {

    private int id;

    public GetDtlPlacesQuery(int id) {
        super((Class<ArrayList<DtlPlace>>) new ArrayList<DtlPlace>().getClass());
        this.id = id;
    }

    @Override
    public ArrayList<DtlPlace> loadDataFromNetwork() {
        ArrayList<DtlPlace> result = getService().getDtlPlaces(id).getPlaces();
        if (result == null)
            result = new ArrayList<>();
        return result;
    }
}
