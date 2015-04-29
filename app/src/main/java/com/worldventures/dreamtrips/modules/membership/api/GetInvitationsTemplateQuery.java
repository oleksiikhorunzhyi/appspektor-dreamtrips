package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import java.util.ArrayList;

public class GetInvitationsTemplateQuery extends Query<ArrayList<InviteTemplate>> {

    public GetInvitationsTemplateQuery() {
        super((Class<ArrayList<InviteTemplate>>) new ArrayList<InviteTemplate>().getClass());
    }

    @Override
    public ArrayList<InviteTemplate> loadDataFromNetwork() throws Exception {
        return getService().getInviteTemplates();
    }
}