package com.worldventures.dreamtrips.modules.friends.events;

import com.worldventures.dreamtrips.modules.common.model.User;

public class HideRequestEvent {
    private User user;
    private int position;

    public HideRequestEvent(User user, int position) {
        this.user = user;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public User getUser() {
        return user;
    }}
