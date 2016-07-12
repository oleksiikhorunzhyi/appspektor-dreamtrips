package com.worldventures.dreamtrips.modules.common.api.janet;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/activities", method = HttpAction.Method.GET)
public class GetActivitiesHttpAction extends AuthorizedHttpAction {

    @Response
    List<ActivityModel> activityModels;

    public List<ActivityModel> getActivityModels() {
        return activityModels;
    }

}
