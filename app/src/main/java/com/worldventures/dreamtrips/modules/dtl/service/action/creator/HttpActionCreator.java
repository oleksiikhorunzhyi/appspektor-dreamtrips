package com.worldventures.dreamtrips.modules.dtl.service.action.creator;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.HttpActionParams;

public interface HttpActionCreator<Action extends AuthorizedHttpAction, Params extends HttpActionParams> {

   Action createAction(Params params);
}
