package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/dtl/v2/merchants/{id}")
public class GetMerchantByIdHttpAction extends AuthorizedHttpAction {

    final @Path("id") String id;

    @Response Merchant merchant;

    public GetMerchantByIdHttpAction(String id) {
        this.id = id;
    }

    public Merchant merchant() {
        return merchant;
    }
}