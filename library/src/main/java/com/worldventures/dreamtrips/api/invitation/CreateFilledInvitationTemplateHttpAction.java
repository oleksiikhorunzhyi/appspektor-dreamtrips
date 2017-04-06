package com.worldventures.dreamtrips.api.invitation;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.FilledInvitationParams;
import com.worldventures.dreamtrips.api.invitation.model.InvitationPreview;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/invitations/filled_templates/", method = POST)
public class CreateFilledInvitationTemplateHttpAction extends AuthorizedHttpAction {

    @Body
    public final FilledInvitationParams params;

    @Response
    InvitationPreview template;

    public CreateFilledInvitationTemplateHttpAction(FilledInvitationParams params) {
        this.params = params;
    }

    public InvitationPreview response() {
        return template;
    }
}
