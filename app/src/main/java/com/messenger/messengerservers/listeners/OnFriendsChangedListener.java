package com.messenger.messengerservers.listeners;

import java.util.List;

public interface OnFriendsChangedListener {

    void onFriendsChangedListener(List<String> userId, boolean isFriend);
}
