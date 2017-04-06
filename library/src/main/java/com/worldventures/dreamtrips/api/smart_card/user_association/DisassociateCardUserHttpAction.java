package com.worldventures.dreamtrips.api.smart_card.user_association;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.RequestHeader;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;
import static io.techery.janet.http.annotations.HttpAction.Type.FORM_URL_ENCODED;

@HttpAction(value = "api/smartcard/provisioning/card_user/{scID}", method = DELETE, type = FORM_URL_ENCODED)
public class DisassociateCardUserHttpAction extends AuthorizedHttpAction {

    @Path("scID")
    public final long scId;

    @Query("device_id")
    public final String deviceId;

    public DisassociateCardUserHttpAction(long scId, String deviceId) {
        this.scId = scId;
        this.deviceId = deviceId;
    }

}
