package com.messenger.delegate;

import android.text.TextUtils;

import com.messenger.delegate.chat.typing.TypingManager;
import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.delegate.conversation.command.SyncConversationCommand;
import com.messenger.entities.DataParticipant;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.worldventures.core.model.session.SessionHolder;

import java.util.Collections;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class GroupChatEventDelegate {

   private final ConversationsDAO conversationsDAO;
   private final ParticipantsDAO participantsDAO;
   private final SessionHolder currentUserSession;
   private final LoadConversationDelegate loadConversationDelegate;
   private final TypingManager typingManager;

   @Inject
   public GroupChatEventDelegate(ConversationsDAO conversationsDAO, ParticipantsDAO participantsDAO,
         SessionHolder currentUserSession, LoadConversationDelegate loadConversationDelegate, TypingManager typingManager) {
      this.conversationsDAO = conversationsDAO;
      this.participantsDAO = participantsDAO;
      this.currentUserSession = currentUserSession;
      this.loadConversationDelegate = loadConversationDelegate;
      this.typingManager = typingManager;
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
