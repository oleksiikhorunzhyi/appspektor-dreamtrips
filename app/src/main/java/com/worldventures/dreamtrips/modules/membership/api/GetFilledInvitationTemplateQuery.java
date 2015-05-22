package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

public class GetFilledInvitationTemplateQuery extends Query<InviteTemplate> {
    private int id;

    public GetFilledInvitationTemplateQuery(int id) {
        super(InviteTemplate.class);
        this.id = id;
    }

    @Override
    public InviteTemplate loadDataFromNetwork() throws Exception {
        return getService().getFilledInviteTemplate(id);
    }
}
