package com.messenger.messengerservers.listeners;

import java.util.Collection;

public interface FriendsRemovedListener {
   void onFriendsRemoved(Collection<String> userIds);
}
