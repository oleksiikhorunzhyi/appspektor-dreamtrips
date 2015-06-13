package com.worldventures.dreamtrips.modules.profile.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

public class GetProfileQuery extends Query<User> {

    private User user;

    public GetProfileQuery(User user) {
        super(User.class);
        this.user = user;
    }

    public GetProfileQuery() {
        super(User.class);
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        return getService().getProfile();
    }
}
