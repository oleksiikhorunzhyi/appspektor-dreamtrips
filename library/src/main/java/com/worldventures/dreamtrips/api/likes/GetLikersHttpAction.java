package com.worldventures.dreamtrips.api.likes;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.likes.model.User;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/{uid}/likes")
public class GetLikersHttpAction extends PaginatedHttpAction {
    @Path("uid")
    public final String uid;

    @Response
    List<User> response;

    public GetLikersHttpAction(String uid, int page, int perPage) {
        super(page, perPage);
        this.uid = uid;
    }

    public List<User> response() {
        return response;
    }
}
