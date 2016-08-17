package com.messenger.delegate.user;

import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.event.JoinedEvent;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.messengerservers.model.Participant;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

@Singleton
public class JoinedChatEventDelegate {

   private final UsersDAO usersDAO;
   private final ParticipantsDAO participantsDAO;
   private final UsersDelegate usersDelegate;

   @Inject
   public JoinedChatEventDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO, UsersDelegate usersDelegate) {
      this.usersDAO = usersDAO;
      this.participantsDAO = participantsDAO;
      this.usersDelegate = usersDelegate;
   }

   public void processJoinedEvents(Observable<JoinedEvent> joinedEventObservable) {
      joinedEventObservable.subscribeOn(Schedulers.io())
            .buffer(3, TimeUnit.SECONDS)
            .filter(joinedEvents -> !joinedEvents.isEmpty())
            .onBackpressureBuffer()
            .doOnNext(this::saveNewParticipants)
            .map(this::filterNotExistedUsersAndUpdateExisted)
            .flatMap(usersDelegate::loadAndSaveUsers)
            .subscribe(dataUsers -> {
            }, throwable -> Timber.e(throwable, ""));
   }


   private void saveNewParticipants(List<JoinedEvent> joinedEvents) {
      List<DataParticipant> participants = from(joinedEvents).map(e -> new DataParticipant(e.getParticipant()))
            .toList();
      participantsDAO.save(participants);
   }

   private List<MessengerUser> filterNotExistedUsersAndUpdateExisted(List<JoinedEvent> joinedEvents) {
      List<DataUser> existedUsers = new ArrayList<>(joinedEvents.size());
      List<MessengerUser> newMessengerUsers = new ArrayList<>(joinedEvents.size());

      for (JoinedEvent e : joinedEvents) {
         Participant participant = e.getParticipant();
         DataUser cachedUser = usersDAO.getUserById(participant.getUserId()).toBlocking().first();
         if (cachedUser != null) {
            cachedUser.setOnline(e.isOnline());
            existedUsers.add(cachedUser);
         } else {
            newMessengerUsers.add(createUser(participant, joinedEvents.isEmpty()));
         }
      }
      usersDAO.save(existedUsers);
      return newMessengerUsers;
   }

   private MessengerUser createUser(Participant participant, boolean isOnline) {
      MessengerUser messengerUser = new MessengerUser(participant.getUserId());
      messengerUser.setOnline(isOnline);
      return messengerUser;
   }
}
