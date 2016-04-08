package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

public class GetMutualFriendsQuery extends Query<ArrayList<User>> {

    private int userId;

    public GetMutualFriendsQuery(int userId) {
        super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
        this.userId = userId;
    }

    @Override
    public ArrayList<User> loadDataFromNetwork() throws Exception {
        return getService().getMutualFriends(userId);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_mutual_friends;
    }
}
