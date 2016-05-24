package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/dtl/v2/merchants")
public class DtlLoadMerchantsAction extends AuthorizedHttpAction {

    @Query("ll")
    String ll;

    @Response
    List<DtlMerchant> response = new ArrayList<>();

    public DtlLoadMerchantsAction(Location location) {
        this.ll = String.format("%1$f,%2$f",
                location.getLatitude(), location.getLongitude());
    }

    public List<DtlMerchant> getResponse() {
        return response;
    }
}
