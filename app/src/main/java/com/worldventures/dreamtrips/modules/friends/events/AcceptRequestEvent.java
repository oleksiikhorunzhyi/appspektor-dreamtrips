package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class AcceptRequestEvent {

    private User user;

    public AcceptRequestEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
