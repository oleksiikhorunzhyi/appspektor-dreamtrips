package com.worldventures.dreamtrips.modules.friends.model;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

public class UserWrapper implements Filterable {
    String[] group;
    User user;

    public String[] getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean containsQuery(String query) {
        return user.getFirstName().contains(query) ||
                user.getLastName().contains(query) ||
                user.getFullName().contains(query) ||
                user.getEmail().contains(query) ||
                user.getLocation().contains(query);
    }
}
