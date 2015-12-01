package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

import java.util.ArrayList;

public class GetDtlPlacesQuery extends DtlRequest<ArrayList<DTlMerchant>> {

    private String id;

    public GetDtlPlacesQuery(String id) {
        super((Class<ArrayList<DTlMerchant>>) new ArrayList<DTlMerchant>().getClass());
        this.id = id;
    }

    @Override
    public ArrayList<DTlMerchant> loadDataFromNetwork() {
        return getService().getDtlPlaces(id);
    }
}
