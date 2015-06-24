package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

public class GetPublicProfileQuery extends Query<User> {

    private User user;

    public GetPublicProfileQuery(User user) {
        super(User.class);
        this.user = user;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        return getService().getPublicProfile(user.getId());
    }
}
