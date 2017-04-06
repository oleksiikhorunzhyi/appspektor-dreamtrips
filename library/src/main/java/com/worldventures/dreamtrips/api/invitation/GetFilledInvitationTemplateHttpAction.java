package com.worldventures.dreamtrips.api.invitation;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.InvitationPreview;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/invitations/filled_templates/{id}")
public class GetFilledInvitationTemplateHttpAction extends AuthorizedHttpAction {

    @Path("id")
    public final int id;

    @Response
    InvitationPreview template;

    public GetFilledInvitationTemplateHttpAction(int id) {
        this.id = id;
    }

    public InvitationPreview response() {
        return template;
    }
}
