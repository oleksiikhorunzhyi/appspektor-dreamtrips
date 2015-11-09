package com.worldventures.dreamtrips.core.utils.events;


import com.worldventures.dreamtrips.modules.common.model.User;

public class UpdateUserInfoEvent {

    public final User user;

    public UpdateUserInfoEvent(User user) {
        this.user = user;
    }

}
