package com.messenger.messengerservers.chat;

import android.support.annotation.NonNull;

import rx.Observable;

public interface ChatManager {

   SingleUserChat createSingleUserChat(@NonNull String companionId, @NonNull String conversationId);

   GroupChat createGroupChat(@NonNull String roomId, String ownerId);

   Observable<GroupChat> createGroupChatObservable(@NonNull String roomId, String ownerId);
}
