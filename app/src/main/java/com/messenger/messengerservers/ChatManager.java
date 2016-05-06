package com.messenger.messengerservers;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.messengerservers.chat.SingleUserChat;

import rx.Observable;

public interface ChatManager {

    SingleUserChat createSingleUserChat(@Nullable String companionId, @Nullable String conversationId);

    GroupChat createGroupChat(@Nullable String roomId, String ownerId, boolean isOwner);

    GroupChat createGroupChat(@Nullable String roomId, String ownerId);

    Observable<GroupChat> createGroupChatObservable(@Nullable String roomId, String ownerId);
}
