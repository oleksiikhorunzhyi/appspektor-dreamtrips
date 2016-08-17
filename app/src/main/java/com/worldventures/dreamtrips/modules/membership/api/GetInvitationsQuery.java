package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.membership.model.History;

import java.util.ArrayList;

public class GetInvitationsQuery extends Query<ArrayList<History>> {

   public GetInvitationsQuery() {
      super((Class<ArrayList<History>>) new ArrayList<History>().getClass());
   }

   @Override
   public ArrayList<History> loadDataFromNetwork() throws Exception {
      return getService().getInvitations();
   }


   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_get_invitations;
   }
}
