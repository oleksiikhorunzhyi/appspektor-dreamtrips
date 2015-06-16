package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class RejectRequestEvent {
    private User user;

    public RejectRequestEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
