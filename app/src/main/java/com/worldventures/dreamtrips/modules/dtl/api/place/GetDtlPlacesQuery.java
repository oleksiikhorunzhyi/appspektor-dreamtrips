package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

import java.util.ArrayList;

public class GetDtlPlacesQuery extends DtlRequest<ArrayList<DtlPlace>> {

    private String id;

    public GetDtlPlacesQuery(String id) {
        super((Class<ArrayList<DtlPlace>>) new ArrayList<DtlPlace>().getClass());
        this.id = id;
    }

    @Override
    public ArrayList<DtlPlace> loadDataFromNetwork() {
        return getService().getDtlPlaces(id);
    }
}
