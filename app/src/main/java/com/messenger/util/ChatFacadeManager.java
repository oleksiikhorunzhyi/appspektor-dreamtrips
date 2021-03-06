package com.messenger.util;

import com.messenger.delegate.GroupChatEventDelegate;
import com.messenger.delegate.chat.ChatMessagesEventDelegate;
import com.messenger.delegate.chat.event.ChatEventInteractor;
import com.messenger.delegate.chat.event.ClearChatCommand;
import com.messenger.delegate.chat.event.RevertClearingChatCommand;
import com.messenger.delegate.user.JoinedChatEventDelegate;
import com.messenger.messengerservers.event.ClearChatEvent;
import com.messenger.messengerservers.event.JoinedEvent;
import com.messenger.messengerservers.event.RevertClearingEvent;
import com.messenger.messengerservers.model.DeletedMessage;
import com.messenger.messengerservers.model.Message;

import java.util.List;

import rx.Observable;

public class ChatFacadeManager {

   private final ChatMessagesEventDelegate chatMessagesDelegate;
   private final GroupChatEventDelegate groupChatEventDelegate;
   private final JoinedChatEventDelegate joinedChatDelegate;
   private final ChatEventInteractor chatEventInteractor;

   public ChatFacadeManager(ChatMessagesEventDelegate chatMessagesDelegate, GroupChatEventDelegate groupChatEventDelegate,
         JoinedChatEventDelegate joinedChatDelegate, ChatEventInteractor chatEventInteractor) {
      this.chatMessagesDelegate = chatMessagesDelegate;
      this.groupChatEventDelegate = groupChatEventDelegate;
      this.joinedChatDelegate = joinedChatDelegate;
      this.chatEventInteractor = chatEventInteractor;
   }

   public void onReceivedMessage(Message message) {
      chatMessagesDelegate.onReceivedMessage(message);
   }

   public void onPreSendMessage(Message message) {
      chatMessagesDelegate.onPreSendMessage(message);
   }

   public void onSendMessage(Message message) {
      chatMessagesDelegate.onSendMessage(message);
   }

   public void onErrorMessage(Message message) {
      chatMessagesDelegate.onErrorMessage(message);
   }

   public void onMessagesDeleted(List<DeletedMessage> deletedMessages) {
      chatMessagesDelegate.onMessagesDeleted(deletedMessages);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////

   public void onChatInvited(String conversationId) {
      groupChatEventDelegate.onChatInvited(conversationId);
   }

   public void onSubjectChanged(String conversationId, String subject) {
      groupChatEventDelegate.onSubjectChanged(conversationId, subject);
   }

   public void onAvatarChanged(String conversationId, String avatar) {
      groupChatEventDelegate.onAvatarChanged(conversationId, avatar);
   }

   public void onChatLeft(String conversationId, String userId) {
      groupChatEventDelegate.onChatLeft(conversationId, userId);
   }

   public void onKicked(String conversationId, String userId) {
      groupChatEventDelegate.onKicked(conversationId, userId);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////

   public void processJoinedEvents(Observable<JoinedEvent> joinedEventObservable) {
      joinedChatDelegate.processJoinedEvents(joinedEventObservable);
   }

   public void onClearChat(ClearChatEvent clearChatEvent) {
      chatEventInteractor.getEventClearChatPipe().send(new ClearChatCommand(clearChatEvent));
   }

   public void onRevertClearing(RevertClearingEvent revertClearingEvent) {
      chatEventInteractor.getEventRevertClearingChatPipe().send(new RevertClearingChatCommand(revertClearingEvent));
   }
}
