package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.common.model.User;

public class ProfileClickedEvent {

    User user;

    public ProfileClickedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
