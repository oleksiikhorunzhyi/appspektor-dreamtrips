package com.worldventures.dreamtrips.api.invitation;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.InvitationTemplate;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/invitations/templates")
public class GetInvitationTemplatesHttpAction extends AuthorizedHttpAction {

    @Query("type")
    public final String type;

    @Response
    List<InvitationTemplate> templates;

    public GetInvitationTemplatesHttpAction() {
        this(null);
    }

    public GetInvitationTemplatesHttpAction(String type) {
        this.type = type;
    }

    public List<InvitationTemplate> response() {
        return templates;
    }

}
