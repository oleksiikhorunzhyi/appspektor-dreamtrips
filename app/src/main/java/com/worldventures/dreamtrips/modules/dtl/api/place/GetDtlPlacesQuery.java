package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.ArrayList;

public class GetDtlPlacesQuery extends DtlRequest<ArrayList<DtlMerchant>> {

    private String id;

    public GetDtlPlacesQuery(String id) {
        super((Class<ArrayList<DtlMerchant>>) new ArrayList<DtlMerchant>().getClass());
        this.id = id;
    }

    @Override
    public ArrayList<DtlMerchant> loadDataFromNetwork() {
        return getService().getDtlPlaces(id);
    }
}
