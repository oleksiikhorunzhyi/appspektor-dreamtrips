package com.messenger.delegate.chat.typing;

import android.support.annotation.NonNull;

import com.messenger.delegate.chat.typing.TypingManager.TypingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MemoryTypingStore implements TypingStore {

   private final PublishSubject<TypingModel> typingUserPublishSubject = PublishSubject.create();

   private final List<TypingModel> typingModels = new CopyOnWriteArrayList<>();

   @Override
   public Observable<List<String>> getTypingUsers(@NonNull String conversationId) {
      return Observable.concat(Observable.just(null), typingUserPublishSubject.filter(typingModel -> conversationId.equals(typingModel
            .getConversationId()))).map(typingModel -> obtainTypingUsers(conversationId));
   }

   private List<String> obtainTypingUsers(String conversationId) {
      List<String> list = new ArrayList<>(5);
      for (TypingModel model : typingModels) {
         if (conversationId.equals(model.getConversationId())) {
            list.add(model.getUserId());
         }
      }
      return list;
   }

   @Override
   public void add(String conversationId, String userId) {
      TypingModel typing = ImmutableTypingModel.builder().conversationId(conversationId).userId(userId).build();
      typingModels.add(typing);
      typingUserPublishSubject.onNext(typing);
   }

   @Override
   public void deleteAll() {
      List<TypingModel> typingModelsCopy;
      synchronized (this) {
         typingModelsCopy = new ArrayList<>(typingModels);
         typingModels.clear();
      }
      remove(typingModelsCopy);
   }

   @Override
   public void delete(@NonNull String conversationId, @NonNull String userId) {
      TypingModel typing = ImmutableTypingModel.builder().conversationId(conversationId).userId(userId).build();
      typingModels.remove(typing);
      typingUserPublishSubject.onNext(typing);
   }

   @Override
   public void deleteByUserId(@NonNull String userId) {
      List<TypingModel> deletedTypingModels = new ArrayList<>();
      synchronized (this) {
         for (TypingModel typing : typingModels) {
            if (userId.equals(typing.getUserId())) {
               deletedTypingModels.add(typing);
            }
         }
      }
      remove(deletedTypingModels);
   }

   private void remove(List<TypingModel> list) {
      for (TypingModel typing : list) {
         typingModels.remove(typing);
         typingUserPublishSubject.onNext(typing);
      }
   }
}
