package com.messenger.ui.model;

import com.messenger.entities.DataUser;

public class SwipeDataUser {
    public final DataUser user;
    public final boolean swipeAvailable;
    public final boolean onlineStatusAvailable;

    public SwipeDataUser(DataUser user, boolean swipeAvailable, boolean onlineStatusAvailable) {
        this.user = user;
        this.swipeAvailable = swipeAvailable;
        this.onlineStatusAvailable = onlineStatusAvailable;
    }
}
