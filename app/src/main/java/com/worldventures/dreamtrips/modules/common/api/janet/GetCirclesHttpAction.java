package com.worldventures.dreamtrips.modules.common.api.janet;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/social/circles", method = HttpAction.Method.GET)
public class GetCirclesHttpAction extends AuthorizedHttpAction {

    @Response
    ArrayList<Circle> circles;

    public ArrayList<Circle> getCircles() {
        return circles;
    }
}
