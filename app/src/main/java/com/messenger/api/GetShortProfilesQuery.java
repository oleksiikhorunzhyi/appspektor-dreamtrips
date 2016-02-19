package com.messenger.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.List;

public class GetShortProfilesQuery extends Query<ArrayList<User>> {

    private List<String> usernames;

    public GetShortProfilesQuery(List<String> usernames) {
        super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
        this.usernames = usernames;
    }

    @Override
    public ArrayList<User> loadDataFromNetwork() throws Exception {
        return getService().getShortProfiles(new ShortProfilesBody(usernames));
    }

}
