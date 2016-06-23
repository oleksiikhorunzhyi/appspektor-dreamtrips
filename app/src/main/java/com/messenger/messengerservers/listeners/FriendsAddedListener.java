package com.messenger.messengerservers.listeners;

import java.util.List;

public interface FriendsAddedListener {
    void onFriendsAdded(List<String> userIds);
}
