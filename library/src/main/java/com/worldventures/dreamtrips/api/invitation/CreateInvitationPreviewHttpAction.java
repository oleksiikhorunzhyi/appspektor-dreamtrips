package com.worldventures.dreamtrips.api.invitation;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.InvitationPreview;
import com.worldventures.dreamtrips.api.invitation.model.PreviewInvitationParams;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/invitations/templates/{id}", method = POST)
public class CreateInvitationPreviewHttpAction extends AuthorizedHttpAction {

    @Path("id")
    public final int id;

    @Body
    public final PreviewInvitationParams params;

    @Response
    InvitationPreview template;

    public CreateInvitationPreviewHttpAction(int id, PreviewInvitationParams params) {
        this.id = id;
        this.params = params;
    }

    public InvitationPreview response() {
        return template;
    }
}
