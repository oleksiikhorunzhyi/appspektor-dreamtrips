package com.worldventures.dreamtrips.api.friends;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate;
import com.worldventures.dreamtrips.api.friends.model.SearchParams;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/social/users")
public class SearchFriendsHttpAction extends PaginatedHttpAction {

    @Query("query")
    public final String query;

    @Response
    List<FriendCandidate> friends;

    public SearchFriendsHttpAction(SearchParams params) {
        super(params);
        this.query = params.query();
    }


    public List<FriendCandidate> response() {
        return friends;
    }
}
