package com.messenger.ui.model;

import com.messenger.entities.DataUser;

public class SwipDataUser {
    public final DataUser user;
    public final boolean swipAvailable;

    public SwipDataUser(DataUser user, boolean swipAvailable) {
        this.user = user;
        this.swipAvailable = swipAvailable;
    }
}
