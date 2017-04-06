package com.worldventures.dreamtrips.api.comment;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.comment.model.Comment;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/{uid}/comments")
public class GetCommentsHttpAction extends PaginatedHttpAction {

    @Path("uid")
    public final String postUid;

    @Response
    List<Comment> comments;

    public GetCommentsHttpAction(String postUid, Integer page, Integer pageSize) {
        super(page, pageSize);
        this.postUid = postUid;
    }

    public List<Comment> response() {
        return comments;
    }
}
