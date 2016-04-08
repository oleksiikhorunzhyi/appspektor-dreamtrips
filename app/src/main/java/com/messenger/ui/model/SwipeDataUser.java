package com.messenger.ui.model;

import com.messenger.entities.DataUser;

public class SwipeDataUser {
    public final DataUser user;
    public final boolean swipeAvailable;

    public SwipeDataUser(DataUser user, boolean swipeAvailable) {
        this.user = user;
        this.swipeAvailable = swipeAvailable;
    }
}
