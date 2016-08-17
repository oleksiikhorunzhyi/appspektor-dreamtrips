package com.messenger.delegate.chat.typing;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

public interface TypingStore {
   Observable<List<String>> getTypingUsers(@NonNull String conversationId);

   void add(String conversationId, String userId);

   void deleteAll();

   void delete(@NonNull String conversationId, @NonNull String userId);

   void deleteByUserId(@NonNull String userId);
}
