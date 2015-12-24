package com.worldventures.dreamtrips.modules.dtl.api.merchant;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.ArrayList;

public class GetNearbyMerchantsRequest extends DtlRequest<ArrayList<DtlMerchant>> {

    private String ll;

    public GetNearbyMerchantsRequest(DtlLocation dtlLocation) {
        super((Class<ArrayList<DtlMerchant>>) new ArrayList<DtlMerchant>().getClass());
        this.ll = String.valueOf(dtlLocation.getCoordinates().getLat())
                + "," + dtlLocation.getCoordinates().getLng();
    }

    @Override
    public ArrayList<DtlMerchant> loadDataFromNetwork() throws Exception {
        return getService().getNearbyDtlMerchants(ll);
    }
}
