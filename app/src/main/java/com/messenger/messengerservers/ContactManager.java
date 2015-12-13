package com.messenger.messengerservers;

import com.messenger.messengerservers.entities.User;

public abstract class ContactManager {
    protected Persister<User> userPersister;

    public void setUserPersister(Persister<User> userPersister) {
        this.userPersister = userPersister;
    }
}
