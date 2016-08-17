package com.messenger.delegate;

import android.text.TextUtils;

import com.messenger.delegate.chat.typing.TypingManager;
import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.delegate.conversation.command.SyncConversationCommand;
import com.messenger.entities.DataParticipant;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Collections;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class GroupChatEventDelegate {

   @Inject MessengerServerFacade messengerServerFacade;
   @Inject ConversationsDAO conversationsDAO;
   @Inject ParticipantsDAO participantsDAO;
   @Inject UsersDAO usersDAO;
   @Inject SessionHolder<UserSession> currentUserSession;
   @Inject LoadConversationDelegate loadConversationDelegate;
   @Inject TypingManager typingManager;

   @Inject
   public GroupChatEventDelegate(@ForApplication Injector injector) {
      injector.inject(this);
   }

   public void onChatInvited(String conversationId) {
      loadConversationDelegate.getSyncConversationPipe().send(new SyncConversationCommand(conversationId));
   }

   public void onSubjectChanged(String conversationId, String subject) {
      conversationsDAO.getConversation(conversationId)
            .first()
            .subscribeOn(Schedulers.io())
            .filter(c -> c != null && !TextUtils.equals(c.getSubject(), subject))
            .subscribe(conversation -> {
               conversation.setSubject(subject);
               conversationsDAO.save(conversation);
            }, throwable -> Timber.d(throwable, ""));
   }

   public void onAvatarChanged(String conversationId, String avatar) {
      conversationsDAO.getConversation(conversationId)
            .take(1)
            .subscribeOn(Schedulers.io())
            .filter(c -> c != null && !TextUtils.equals(c.getAvatar(), avatar))
            .subscribe(conversation -> {
               conversation.setAvatar(avatar);
               conversationsDAO.save(conversation);
            }, e -> Timber.d(e, "Could not save avatar to conversation"));
   }

   public void onChatLeft(String conversationId, String userId) {
      handleRemovingMember(conversationId, userId);
   }

   public void onKicked(String conversationId, String userId) {
      handleRemovingMember(conversationId, userId);
   }

   private void handleRemovingMember(String conversationId, String userId) {
      Observable.fromCallable(() -> removeFromConversation(conversationId, userId))
            .flatMap(dataParticipant -> loadConversationDelegate.getSyncConversationPipe()
                  .createObservableResult(new SyncConversationCommand(conversationId)))
            .subscribeOn(Schedulers.io())
            .subscribe(p -> {}, e -> Timber.e(e, ""));
   }

   private DataParticipant removeFromConversation(String conversationId, String userId) {
      typingManager.userStopTyping(conversationId, userId);
      DataParticipant participant = new DataParticipant(conversationId, userId, Affiliation.NONE);
      participantsDAO.save(Collections.singletonList(participant));

      if (TextUtils.equals(userId, currentUserSession.get().get().getUsername())) {
         conversationsDAO.markAsLeft(conversationId);
      }
      return participant;
   }

}
