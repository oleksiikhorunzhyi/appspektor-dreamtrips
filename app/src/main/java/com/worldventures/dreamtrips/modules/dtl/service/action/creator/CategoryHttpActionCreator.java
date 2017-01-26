package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.HttpActionParams;

import java.util.List;

/**
 * Created by yair.carreno on 1/24/2017.
 */

public interface CategoryHttpActionCreator<Action extends AuthorizedHttpAction, Params extends HttpActionParams> {

   Action createAction(Params params, List<String> merchantTypes);
}
