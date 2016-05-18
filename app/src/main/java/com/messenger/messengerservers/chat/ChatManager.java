package com.messenger.messengerservers.chat;

import android.support.annotation.Nullable;

import rx.Observable;

public interface ChatManager {

    SingleUserChat createSingleUserChat(@Nullable String companionId, @Nullable String conversationId);

    GroupChat createGroupChat(@Nullable String roomId, String ownerId);

    Observable<GroupChat> createGroupChatObservable(@Nullable String roomId, String ownerId);
}
