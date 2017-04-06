package com.worldventures.dreamtrips.api.invitation;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.Invitation;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("api/invitations/")
public class GetInvitationsHistoryHttpAction extends AuthorizedHttpAction {

    @Response
    List<Invitation> invitations;

    public List<Invitation> response() {
        return invitations;
    }

}
