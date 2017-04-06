package com.worldventures.dreamtrips.api.smart_card.location;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.GET;
import static io.techery.janet.http.annotations.HttpAction.Type.FORM_URL_ENCODED;


@HttpAction(value = "api/smartcard/provisioning/card_data/{scID}/locations", method = GET)
public class GetSmartCardLocationsHttpAction extends AuthorizedHttpAction {

    @Path("scID")
    public final long scId;

    @Response
    List<SmartCardLocation> locationResponse;

    public GetSmartCardLocationsHttpAction(long scId) {
        this.scId = scId;
    }

    public List<SmartCardLocation> response() {
        return locationResponse;
    }
}
