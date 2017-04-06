package com.worldventures.dreamtrips.api.smart_card.user_info;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.PUT;

@HttpAction(value = "api/smartcard/provisioning/card_user/{scID}", method = PUT)
public class UpdateCardUserHttpAction extends AuthorizedHttpAction {

    @Path("scID")
    public final long scId;

    @Body
    public final UpdateCardUserData data;

    public UpdateCardUserHttpAction(long scId, UpdateCardUserData data) {
        this.scId = scId;
        this.data = data;
    }

}
