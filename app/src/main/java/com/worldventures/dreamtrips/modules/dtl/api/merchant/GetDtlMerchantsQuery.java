package com.worldventures.dreamtrips.modules.dtl.api.merchant;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.ArrayList;

public class GetDtlMerchantsQuery extends DtlRequest<ArrayList<DtlMerchant>> {

    private String id;

    public GetDtlMerchantsQuery(String id) {
        super((Class<ArrayList<DtlMerchant>>) new ArrayList<DtlMerchant>().getClass());
        this.id = id;
    }

    @Override
    public ArrayList<DtlMerchant> loadDataFromNetwork() {
        return getService().getDtlMerchants(id);
    }
}
