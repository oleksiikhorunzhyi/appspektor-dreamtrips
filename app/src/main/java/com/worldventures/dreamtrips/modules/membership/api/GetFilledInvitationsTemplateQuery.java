package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

public class GetFilledInvitationsTemplateQuery extends Query<InviteTemplate> {
    private int id;
    private String message;

    public GetFilledInvitationsTemplateQuery(int id, String message) {
        super(InviteTemplate.class);
        this.id = id;
        this.message = message;
    }

    @Override
    public InviteTemplate loadDataFromNetwork() throws Exception {
        return getService().getFilledInviteTemplate(id, message);
    }
}
