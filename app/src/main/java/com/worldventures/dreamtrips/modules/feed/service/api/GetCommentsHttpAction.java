package com.worldventures.dreamtrips.modules.feed.service.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/{uid}/comments")
public class GetCommentsHttpAction extends AuthorizedHttpAction {

    @Path("uid") public final String postUid;
    @Query("page") public final int page;
    @Query("per_page") public final int perPage;

    @Response List<Comment> comments;

    public GetCommentsHttpAction(String postUid, int page, int perPage) {
        this.postUid = postUid;
        this.page = page;
        this.perPage = perPage;
    }

    public List<Comment> response() {
        return comments;
    }
}
