package com.messenger.delegate.user;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.UserType;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserEventsDelegate {

    private final UsersDAO usersDAO;
    private final UsersDelegate usersDelegate;

    @Inject
    public UserEventsDelegate(UsersDAO usersDAO, UsersDelegate usersDelegate) {
        this.usersDAO = usersDAO;
        this.usersDelegate = usersDelegate;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Friends related
    ///////////////////////////////////////////////////////////////////////////

    public void friendsAdded(List<String> userIds) {
        Observable.from(userIds)
                .map(userId -> {
                    MessengerUser messengerUser = new MessengerUser(userId);
                    messengerUser.setType(UserType.FRIEND);

                    return messengerUser;
                }).toList()
                .flatMap(usersDelegate::loadUsers)
                .subscribe(usersDAO::save, e -> Timber.e(e, "Failed to add friend"));
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
