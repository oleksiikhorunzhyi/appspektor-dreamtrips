package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class CancelRequestEvent {
    private User user;

    public CancelRequestEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
