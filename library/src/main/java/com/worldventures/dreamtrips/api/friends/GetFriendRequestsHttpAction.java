package com.worldventures.dreamtrips.api.friends;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate;
import com.worldventures.dreamtrips.api.friends.model.GetFriendRequestsParams;
import com.worldventures.dreamtrips.api.friends.model.ImmutableGetFriendRequestsParams;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/social/friends/requests")
public class GetFriendRequestsHttpAction extends PaginatedHttpAction {

    @Response
    List<FriendCandidate> friends;

    public GetFriendRequestsHttpAction(GetFriendRequestsParams params) {
        super(params);
    }

    public GetFriendRequestsHttpAction() {
        super(ImmutableGetFriendRequestsParams.builder().page(1).perPage(100).build());
    }

    public List<FriendCandidate> response() {
        return friends;
    }
}
