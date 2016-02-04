package com.messenger.messengerservers;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.chat.SingleUserChat;

import rx.Observable;


public interface ChatManager {

    SingleUserChat createSingleUserChat(@Nullable String companionId, @Nullable String conversationId);

    MultiUserChat createMultiUserChat(@Nullable String roomId, String ownerId, boolean isOwner);

    MultiUserChat createMultiUserChat(@Nullable String roomId, String ownerId);

    Observable<MultiUserChat> createMultiUserChatObservable(@Nullable String roomId, String ownerId);
}
