package com.messenger.delegate;

import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.event.JoinedEvent;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.messengerservers.model.Participant;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;
import static rx.Observable.just;

public class JoinedChatEventDelegate {

    @Inject
    UsersDAO usersDAO;
    @Inject
    ParticipantsDAO participantsDAO;

    @Inject
    UserProcessor userProcessor;

    @Inject
    public JoinedChatEventDelegate(@ForApplication Injector injector) {
        injector.inject(this);
    }

    public void processJoinedEvents (Observable<JoinedEvent> joinedEventObservable){
        joinedEventObservable
                .subscribeOn(Schedulers.io())
                .buffer(3, TimeUnit.SECONDS)
                .filter(joinedEvents -> !joinedEvents.isEmpty())
                .onBackpressureBuffer()
                .doOnNext(this::saveNewParticipants)
                .map(this::filterNotExistedUsersAndUpdateExisted)
                .flatMap(users -> userProcessor.connectToUserProvider(just(users)))
                .doOnNext(usersDAO::save)
                .subscribe(dataUsers -> {
                }, throwable -> Timber.d(throwable, ""));
    }

    private MessengerUser createUser(Participant participant, boolean isOnline) {
        MessengerUser messengerUser = new MessengerUser(participant.getUserId());
        messengerUser.setOnline(isOnline);
        return messengerUser;
    }

    private void saveNewParticipants(List<JoinedEvent> joinedEvents) {
        List<DataParticipant> participants = from(joinedEvents).map(e -> new DataParticipant(e.getParticipant())).toList();
        participantsDAO.save(participants);
    }

    private List<MessengerUser> filterNotExistedUsersAndUpdateExisted(List<JoinedEvent> joinedEvents) {
        List<DataUser> existedUsers = new ArrayList<>(joinedEvents.size());
        List<MessengerUser> newMessengerUsers = new ArrayList<>(joinedEvents.size());

        for (JoinedEvent e: joinedEvents) {
            Participant participant = e.getParticipant();
            DataUser cachedUser = usersDAO.getUserById(participant.getUserId()).toBlocking().first();
            if (cachedUser != null) {
                cachedUser.setOnline(e.isOnline());
                existedUsers.add(cachedUser);
            }
            else {
                newMessengerUsers.add(createUser(participant, joinedEvents.isEmpty()));
            }
        }
        usersDAO.save(existedUsers);
        return newMessengerUsers;
    }

}
