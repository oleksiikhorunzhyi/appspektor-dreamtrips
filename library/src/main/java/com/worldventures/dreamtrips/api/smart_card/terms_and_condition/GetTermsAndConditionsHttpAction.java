package com.worldventures.dreamtrips.api.smart_card.terms_and_condition;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/smartcard/provisioning/terms_and_conditions", method = HttpAction.Method.GET)
public class GetTermsAndConditionsHttpAction extends AuthorizedHttpAction {

    @Response
    TermsAndConditions response;

    public TermsAndConditions response() {
        return response;
    }
}