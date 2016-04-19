package com.messenger.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.UserType;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class UserDelegate {

    private UserProcessor userProcessor;
    private UsersDAO usersDAO;

    @Inject
    public UserDelegate(UserProcessor userProcessor, UsersDAO usersDAO) {
        this.userProcessor = userProcessor;
        this.usersDAO = usersDAO;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Friends related
    ///////////////////////////////////////////////////////////////////////////

    public void friendsAdded(Collection<String> userIds) {
        Queryable.from(userIds).forEachR(this::friendAdded);
    }

    private void friendAdded(String userId) {
        getUser(userId)
                .doOnNext(user -> {
                    if (user == null) loadFriend(userId);
                })
                .compose(new NonNullFilter<>())
                .subscribe(user -> {
                    user.setFriend(true);
                    usersDAO.save(user);
                });
    }

    public void friendsRemoved(Collection<String> userIds) {
        Queryable.from(userIds).forEachR(this::friendRemoved);
    }

    private void friendRemoved(String userId) {
        getUser(userId)
                .compose(new NonNullFilter<>())
                .subscribe(user -> {
                    user.setFriend(false);
                    usersDAO.save(user);
                });
    }

    private void loadFriend(String userId) {
        MessengerUser messengerUser = new MessengerUser(userId);
        messengerUser.setType(UserType.FRIEND);
        userProcessor.connectToUserProvider(Observable
                .just(Collections.singletonList(messengerUser)));
    }


    ///////////////////////////////////////////////////////////////////////////
    // Presence
    ///////////////////////////////////////////////////////////////////////////

    public void presenceChanged(String userId, boolean isOnline) {
        getUser(userId)
                .compose(new NonNullFilter<>())
                .subscribe(user -> {
                    user.setOnline(isOnline);
                    usersDAO.save(user);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private Observable<DataUser> getUser(String userId) {
        return usersDAO.getUserById(userId)
                .take(1)
                .subscribeOn(Schedulers.io());
    }

}
