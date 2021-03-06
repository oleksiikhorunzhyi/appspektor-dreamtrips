package com.messenger.messengerservers;

import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.event.ClearChatEvent;
import com.messenger.messengerservers.event.JoinedEvent;
import com.messenger.messengerservers.event.RevertClearingEvent;
import com.messenger.messengerservers.listeners.FriendsAddedListener;
import com.messenger.messengerservers.listeners.FriendsRemovedListener;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.listeners.InvitationListener;
import com.messenger.messengerservers.listeners.MessagesDeletedListener;
import com.messenger.messengerservers.listeners.OnAvatarChangedListener;
import com.messenger.messengerservers.listeners.OnChatJoinedListener;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;
import com.messenger.messengerservers.listeners.OnKickListener;
import com.messenger.messengerservers.listeners.OnSubjectChangedListener;
import com.messenger.messengerservers.listeners.PresenceListener;
import com.messenger.messengerservers.model.DeletedMessage;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.Participant;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.functions.Action1;

public abstract class GlobalEventEmitter {

   protected static final int EVENT_PRE_OUTGOING = 0x777;
   protected static final int EVENT_OUTGOING = 0x778;
   protected static final int EVENT_INCOMING = 0x779;
   protected static final int EVENT_OUTGOING_ERROR = 0x780;
   private final List<GlobalMessageListener> globalMessageListeners = new CopyOnWriteArrayList<>();
   private final List<PresenceListener> presenceListeners = new CopyOnWriteArrayList<>();
   private final List<FriendsAddedListener> friendsAddedListeners = new CopyOnWriteArrayList<>();
   private final List<FriendsRemovedListener> friendsRemovedListeners = new CopyOnWriteArrayList<>();
   private final List<InvitationListener> invitationListeners = new CopyOnWriteArrayList<>();
   private final List<OnSubjectChangedListener> onSubjectChangedListeners = new CopyOnWriteArrayList<>();
   private final List<OnAvatarChangedListener> onAvatarChangedListeners = new CopyOnWriteArrayList<>();
   private final List<OnChatLeftListener> onChatLeftListeners = new CopyOnWriteArrayList<>();
   private final List<OnKickListener> onKickListeners = new CopyOnWriteArrayList<>();
   private final List<OnChatJoinedListener> onChatJoinedListeners = new CopyOnWriteArrayList<>();
   private final List<OnChatStateChangedListener> onChatStateChangedListeners = new CopyOnWriteArrayList<>();
   private final List<MessagesDeletedListener> onMessagesDeletedListener = new CopyOnWriteArrayList<>();
   private final List<Action1<ClearChatEvent>> onClearChatEventListeners = new CopyOnWriteArrayList<>();
   private final List<Action1<RevertClearingEvent>> onRevertClearingEventListeners = new CopyOnWriteArrayList<>();

   public void addGlobalMessageListener(GlobalMessageListener listener) {
      globalMessageListeners.add(listener);
   }

   public void removeGlobalMessageListener(GlobalMessageListener listener) {
      globalMessageListeners.remove(listener);
   }

   protected void notifyGlobalMessage(Message message, int eventType) {
      switch (eventType) {
         case EVENT_PRE_OUTGOING:
            for (GlobalMessageListener listener : globalMessageListeners) {
               listener.onPreSendMessage(message);
            }
            break;
         case EVENT_OUTGOING:
            for (GlobalMessageListener listener : globalMessageListeners) {
               listener.onSendMessage(message);
            }
            break;
         case EVENT_INCOMING:
            for (GlobalMessageListener listener : globalMessageListeners) {
               listener.onReceiveMessage(message);
            }
            break;
         case EVENT_OUTGOING_ERROR:
            for (GlobalMessageListener listener : globalMessageListeners) {
               listener.onErrorMessage(message);
            }
            break;
         default:
            break;
      }
   }

   public void addFriendAddedListener(FriendsAddedListener listener) {
      friendsAddedListeners.add(listener);
   }

   protected void notifyFriendsAdded(Collection<String> userIds) {
      for (FriendsAddedListener listener : friendsAddedListeners) {
         listener.onFriendsAdded(userIds);
      }
   }

   public void addFriendRemovedListener(FriendsRemovedListener listener) {
      friendsRemovedListeners.add(listener);
   }

   protected void notifyFriendsRemoved(Collection<String> userIds) {
      for (FriendsRemovedListener listener : friendsRemovedListeners) {
         listener.onFriendsRemoved(userIds);
      }
   }

   public void addPresenceListener(PresenceListener listener) {
      presenceListeners.add(listener);
   }

   public void removePresenceListener(PresenceListener listener) {
      presenceListeners.remove(listener);
   }

   protected void notifyUserPresenceChanged(String userId, boolean isOnline) {
      for (PresenceListener listener : presenceListeners) {
         listener.onUserPresenceChanged(userId, isOnline);
      }
   }

   public void addInvitationListener(InvitationListener listener) {
      invitationListeners.add(listener);
   }

   public void removeInvitationListener(InvitationListener listener) {
      invitationListeners.remove(listener);
   }

   protected void notifyReceiveInvite(String roomId) {
      for (InvitationListener listener : invitationListeners) {
         listener.receiveInvite(roomId);
      }
   }

   public void addOnSubjectChangesListener(OnSubjectChangedListener listener) {
      onSubjectChangedListeners.add(listener);
   }

   public void removeOnSubjectChangesListener(OnSubjectChangedListener listener) {
      onSubjectChangedListeners.remove(listener);
   }

   protected void notifyOnSubjectChanges(String conversationId, String subject) {
      for (OnSubjectChangedListener listener : onSubjectChangedListeners) {
         listener.onSubjectChanged(conversationId, subject);
      }
   }

   public void addOnAvatarChangeListener(OnAvatarChangedListener listener) {
      onAvatarChangedListeners.add(listener);
   }

   public void removeOnAvatarChangeListener(OnAvatarChangedListener listener) {
      onAvatarChangedListeners.remove(listener);
   }

   protected void notifyOnAvatarStateChangedListener(String conversationId, String avatar) {
      for (OnAvatarChangedListener listener : onAvatarChangedListeners) {
         listener.onAvatarChangedListener(conversationId, avatar);
      }
   }

   public void addOnChatLeftListener(OnChatLeftListener listener) {
      onChatLeftListeners.add(listener);
   }

   public void removeOnChatLeftListener(OnChatLeftListener listener) {
      onChatLeftListeners.remove(listener);
   }

   protected void notifyOnChatLeftListener(String conversationId, String userId) {
      for (OnChatLeftListener listener : onChatLeftListeners) {
         listener.onChatLeft(conversationId, userId);
      }
   }

   public void addOnKickListener(OnKickListener listener) {
      onKickListeners.add(listener);
   }

   public void removeOnKickListener(OnKickListener listener) {
      onKickListeners.remove(listener);
   }

   protected void notifyOnKickListener(String conversationId, String userId) {
      for (OnKickListener l : onKickListeners) {
         l.onKicked(conversationId, userId);
      }
   }

   public void addOnChatJoinedListener(OnChatJoinedListener listener) {
      onChatJoinedListeners.add(listener);
   }

   public void removeOnChatJoinedListener(OnChatJoinedListener listener) {
      onChatJoinedListeners.remove(listener);
   }

   protected void notifyOnChatJoinedListener(Participant participant, boolean isOnline) {
      for (OnChatJoinedListener listener : onChatJoinedListeners) {
         listener.onChatJoined(participant, isOnline);
      }
   }

   public void addOnChatStateChangedListener(OnChatStateChangedListener listener) {
      onChatStateChangedListeners.add(listener);
   }

   public void removeOnChatStateChangedListener(OnChatStateChangedListener listener) {
      onChatStateChangedListeners.remove(listener);
   }

   protected void notifyOnChatStateChangedListener(String conversationId, String userId, @ChatState.State String state) {
      for (OnChatStateChangedListener listener : onChatStateChangedListeners) {
         listener.onChatStateChanged(conversationId, userId, state);
      }
   }

   public void addOnMessagesDeletedListener(MessagesDeletedListener listener) {
      onMessagesDeletedListener.add(listener);
   }

   protected void notifyMessagesDeleted(List<DeletedMessage> deletedMessages) {
      for (MessagesDeletedListener listener : onMessagesDeletedListener) {
         listener.onMessagesDeleted(deletedMessages);
      }
   }


   public void addOnRevertClearingEventListener(Action1<RevertClearingEvent> listener) {
      onRevertClearingEventListeners.add(listener);
   }

   public void removeOnRevertClearingEventListener(Action1<RevertClearingEvent> listener) {
      onRevertClearingEventListeners.remove(listener);
   }

   protected void notifyOnRevertClearingEventListener(RevertClearingEvent event) {
      for (Action1<RevertClearingEvent> listener : onRevertClearingEventListeners) {
         listener.call(event);
      }
   }


   public void addOnClearChatEventListener(Action1<ClearChatEvent> listener) {
      onClearChatEventListeners.add(listener);
   }

   public void removeOnClearChatEventListener(Action1<ClearChatEvent> listener) {
      onClearChatEventListeners.remove(listener);
   }

   protected void notifyOnClearChatEventListener(ClearChatEvent event) {
      for (Action1<ClearChatEvent> listener : onClearChatEventListeners) {
         listener.call(event);
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Observables
   ///////////////////////////////////////////////////////////////////////////

   public Observable<JoinedEvent> createChatJoinedObservable() {
      OnChatJoinedListener[] onChatJoinedListeners = new OnChatJoinedListener[]{null};
      return Observable.<JoinedEvent>create(subscriber -> {
         OnChatJoinedListener listener = (participant, isOnline) -> {
            subscriber.onNext(new JoinedEvent(participant, isOnline));
         };
         addOnChatJoinedListener(listener);
         onChatJoinedListeners[0] = listener;
      }).doOnUnsubscribe(() -> removeOnChatJoinedListener(onChatJoinedListeners[0]));
   }
}
