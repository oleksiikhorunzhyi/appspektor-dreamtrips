package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

public class GetPublicProfileQuery extends Query<Friend> {

    private Friend user;

    public GetPublicProfileQuery(Friend user) {
        super(Friend.class);
        this.user = user;
    }

    @Override
    public Friend loadDataFromNetwork() throws Exception {
        return getService().getPublicProfile(user.getId());
    }
}
