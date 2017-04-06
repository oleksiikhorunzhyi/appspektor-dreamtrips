package com.worldventures.dreamtrips.api.smart_card.bank_info;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.bank_info.model.BankInfo;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/smartcard/bankinfo/{iin}", method = HttpAction.Method.GET)
public class GetBankInfoHttpAction extends AuthorizedHttpAction {

    @Path("iin")
    public final long iin;

    @Response
    BankInfo response;

    public GetBankInfoHttpAction(long iin) {this.iin = iin;}

    public BankInfo response() {
        return response;
    }
}