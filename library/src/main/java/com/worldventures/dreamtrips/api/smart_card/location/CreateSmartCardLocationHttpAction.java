package com.worldventures.dreamtrips.api.smart_card.location;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;


@HttpAction(value = "api/smartcard/provisioning/card_data/{scID}/locations", method = POST)
public class CreateSmartCardLocationHttpAction extends AuthorizedHttpAction {

    @Path("scID")
    public final long scId;

    @Body
    public final SmartCardLocationBody smartCardLocationBody;

    public CreateSmartCardLocationHttpAction(long scId, SmartCardLocationBody smartCardLocationBody) {
        this.scId = scId;
        this.smartCardLocationBody = smartCardLocationBody;
    }
}
