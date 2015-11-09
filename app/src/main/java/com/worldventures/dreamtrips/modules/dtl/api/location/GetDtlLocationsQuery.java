package com.worldventures.dreamtrips.modules.dtl.api.location;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.ArrayList;

public class GetDtlLocationsQuery extends DtlRequest<ArrayList<DtlLocation>> {

    private String keyword;

    public GetDtlLocationsQuery(String keyword) {
        super((Class<ArrayList<DtlLocation>>) new ArrayList<DtlLocation>().getClass());
        this.keyword = keyword;
    }

    @Override
    public ArrayList<DtlLocation> loadDataFromNetwork() {
        return getService().searchDtlLocations(keyword);
    }
}
