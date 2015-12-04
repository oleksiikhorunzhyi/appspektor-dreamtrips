package com.messenger.messengerservers.chat;

import java.util.List;

import com.messenger.messengerservers.entities.User;

public abstract class MultiUserChat extends Chat {
    public abstract void leave();

    public abstract void join(User user);

    public abstract void invite(List<User> users);
}
