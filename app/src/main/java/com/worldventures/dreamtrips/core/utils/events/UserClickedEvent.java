package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class UserClickedEvent {

    User user;

    public UserClickedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
