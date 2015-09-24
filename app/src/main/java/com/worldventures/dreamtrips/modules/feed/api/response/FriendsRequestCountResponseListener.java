package com.worldventures.dreamtrips.modules.feed.api.response;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;

public class FriendsRequestCountResponseListener extends RequestCountResponseListener {

    public FriendsRequestCountResponseListener(SnappyRepository db) {
        super(SnappyRepository.FRIEND_REQUEST_COUNT, db);
    }

}
