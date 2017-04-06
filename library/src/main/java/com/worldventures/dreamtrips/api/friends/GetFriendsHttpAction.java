package com.worldventures.dreamtrips.api.friends;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendProfile;
import com.worldventures.dreamtrips.api.friends.model.FriendsParams;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/social/friends")
public class GetFriendsHttpAction extends PaginatedHttpAction {

    @Query("query")
    public final String query;

    @Query("circle_id")
    public final String circleId;

    @Response
    List<FriendProfile> friends;

    public GetFriendsHttpAction(FriendsParams params) {
        super(params);
        this.query = params.query();
        this.circleId = params.circleId();
    }

    public List<FriendProfile> response() {
        return friends;
    }
}
