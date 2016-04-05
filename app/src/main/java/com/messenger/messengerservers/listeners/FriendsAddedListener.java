package com.messenger.messengerservers.listeners;

import java.util.Collection;

public interface FriendsAddedListener {
    void onFriendsAdded(Collection<String> userIds);
}
