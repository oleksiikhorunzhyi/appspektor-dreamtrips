package com.worldventures.dreamtrips.api.smart_card.nxt;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.smart_card.nxt.model.NxtSession;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "api/smartcard/tokenization/session", method = POST)
public class CreateNxtSessionHttpAction extends AuthorizedHttpAction {

    @Body
    String body = "";

    @Response
    NxtSession session;

    public NxtSession response() {
        return session;
    }
}
