package com.worldventures.dreamtrips.api.invitation;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.CreateInvitationParams;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "api/invitations/", method = POST)
public class CreateInvitationHttpAction extends AuthorizedHttpAction {

    @Body
    public final CreateInvitationParams params;

    public CreateInvitationHttpAction(CreateInvitationParams params) {
        this.params = params;
    }
}
