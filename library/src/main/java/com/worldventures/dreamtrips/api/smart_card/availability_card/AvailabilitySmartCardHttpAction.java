package com.worldventures.dreamtrips.api.smart_card.availability_card;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.GET;

@HttpAction(value = "api/smartcard/provisioning/card_user/{scID}", method = GET)
public class AvailabilitySmartCardHttpAction extends AuthorizedHttpAction {

    @Path("scID")
    public final String scId;

    public AvailabilitySmartCardHttpAction(String scId) {
        this.scId = scId;
    }
}
