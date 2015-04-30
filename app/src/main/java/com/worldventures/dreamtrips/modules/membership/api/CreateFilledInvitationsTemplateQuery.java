package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

public class CreateFilledInvitationsTemplateQuery extends Query<InviteTemplate> {
    private int id;
    private String message;

    public CreateFilledInvitationsTemplateQuery(int id, String message) {
        super(InviteTemplate.class);
        this.id = id;
        this.message = message;
    }

    @Override
    public InviteTemplate loadDataFromNetwork() throws Exception {
        return getService().createInviteTemplate(id, message);
    }
}
