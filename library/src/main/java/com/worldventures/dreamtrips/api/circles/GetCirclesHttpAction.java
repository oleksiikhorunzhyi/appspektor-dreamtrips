package com.worldventures.dreamtrips.api.circles;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.circles.model.Circle;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/social/circles")
public class GetCirclesHttpAction extends AuthorizedHttpAction {

    @Response
    List<Circle> circles;

    public List<Circle> response() {
        return circles;
    }
}
