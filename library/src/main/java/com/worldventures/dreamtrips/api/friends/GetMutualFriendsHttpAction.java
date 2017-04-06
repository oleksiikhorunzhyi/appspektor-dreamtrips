package com.worldventures.dreamtrips.api.friends;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate;
import com.worldventures.dreamtrips.api.friends.model.ImmutableMutualFriendsParams;
import com.worldventures.dreamtrips.api.friends.model.MutualFriendsParams;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/social/friends/{user_id}/mutual/")
public class GetMutualFriendsHttpAction extends PaginatedHttpAction {

    @Path("user_id")
    public final int userId;

    @Response
    List<FriendCandidate> friends;

    public GetMutualFriendsHttpAction(MutualFriendsParams params) {
        super(params);
        this.userId = params.userId();
    }

    @Deprecated
    /**
     * Default constructor for backward compatibility, remove later
     */
    public GetMutualFriendsHttpAction(int userId) {
        this(ImmutableMutualFriendsParams.builder().userId(userId)
            .page(1).perPage(100).build());
    }

    public List<FriendCandidate> response() {
        return friends;
    }
}
