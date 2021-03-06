package com.messenger.delegate;

import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.converter.UserConverter;
import com.messenger.delegate.conversation.helper.CreateConversationHelper;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

public class StartChatDelegate {

   private final static String ERROR_MESSAGE = "Error";

   private final UsersDAO usersDAO;
   private final ParticipantsDAO participantsDAO;
   private final ConversationsDAO conversationsDAO;
   private final CreateConversationHelper createConversationHelper;

   public StartChatDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO, ConversationsDAO conversationsDAO, CreateConversationHelper createConversationHelper) {
      this.usersDAO = usersDAO;
      this.participantsDAO = participantsDAO;
      this.conversationsDAO = conversationsDAO;
      this.createConversationHelper = createConversationHelper;
   }

   public void startSingleChat(User user, @NotNull Action1<DataConversation> crossingAction) {
      if (user.getUsername() == null) {
         return;
      }

      usersDAO.getUserById(user.getUsername())
            .first()
            .map(participant -> {
               if (participant == null) {
                  participant = UserConverter.convert(user);
                  usersDAO.save(Collections.singletonList(participant));
               }
               return participant;
            })
            .flatMap(this::startSingleChatObservable)
            .compose(new IoToMainComposer<>())
            .subscribe(crossingAction, throwable -> Timber.e(throwable, ERROR_MESSAGE));
   }

   public void startSingleChat(String userId, @NotNull Action1<DataConversation> crossingAction) {
      usersDAO.getUserById(userId)
            .take(1)
            .flatMap(this::startSingleChatObservable)
            .compose(new IoToMainComposer<>())
            .subscribe(crossingAction, throwable -> Timber.e(throwable, ERROR_MESSAGE));
   }

   public void startSingleChat(DataUser user, @NotNull Action1<DataConversation> crossingAction, @Nullable Action1<Throwable> errorAction) {
      if (errorAction == null) {
         errorAction = throwable -> Timber.e(throwable, ERROR_MESSAGE);
      }
      startSingleChatObservable(user).compose(new IoToMainComposer<>()).subscribe(crossingAction, errorAction);
   }

   private Observable<DataConversation> startSingleChatObservable(DataUser participant) {
      DataConversation conversation = createConversationHelper.getExistingSingleConversation(participant.getId());
      if (conversation != null) {
         return Observable.just(conversation);
      }

      return createConversationHelper.createNewConversation(Collections.singletonList(participant), "")
            .doOnNext(dataConversation -> {
               //there is no owners in single chat
               DataParticipant relationship = new DataParticipant(dataConversation.getId(), participant.getId(), Affiliation.MEMBER);

               participantsDAO.save(Collections.singletonList(relationship));
               conversationsDAO.save(Collections.singletonList(dataConversation));
            });
   }

   public void startNewGroupChat(String ownerId, List<DataUser> participant, @Nullable String subject, @NotNull Action1<DataConversation> crossingAction, @Nullable Action1<Throwable> errorAction) {
      if (errorAction == null) {
         errorAction = throwable -> Timber.d(throwable, ERROR_MESSAGE);
      }
      createConversationHelper.createNewConversation(participant, subject).doOnNext(conversation -> {
         conversation.setOwnerId(ownerId);
         List<DataParticipant> relationships = Queryable.from(participant)
               .map(user -> new DataParticipant(conversation.getId(), user.getId(), Affiliation.MEMBER))
               .toList();
         // we are participants too and if conversation is group then we're owner otherwise we're member
         relationships.add(new DataParticipant(conversation.getId(), ownerId, Affiliation.OWNER));

         participantsDAO.save(relationships);
         conversationsDAO.save(Collections.singletonList(conversation));
      }).compose(new IoToMainComposer<>()).subscribe(crossingAction, errorAction);
   }

}
