package com.worldventures.dreamtrips.modules.feed.model.feed.item;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;
import java.util.List;

public class Links implements Serializable {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }
}
