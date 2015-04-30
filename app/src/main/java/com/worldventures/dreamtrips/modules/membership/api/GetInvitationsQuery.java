package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.History;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import java.util.ArrayList;

public class GetInvitationsQuery extends Query<ArrayList<History>> {

    public GetInvitationsQuery() {
        super((Class<ArrayList<History>>) new ArrayList<History>().getClass());
    }

    @Override
    public ArrayList<History> loadDataFromNetwork() throws Exception {
        return getService().getInvitations();
    }
}
