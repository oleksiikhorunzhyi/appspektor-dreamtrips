package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlMerchantsAction extends CallableCommandAction<List<DtlMerchant>> {

    public DtlMerchantsAction(DtlApi dtlApi, String location) {
        super(() -> dtlApi.getNearbyDtlMerchants(location));
    }

    public DtlMerchantsAction(List<DtlMerchant> merchants) {
        super(() -> merchants);
    }
}
