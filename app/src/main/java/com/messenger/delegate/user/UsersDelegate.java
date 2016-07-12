package com.messenger.delegate.user;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.UsersDAO;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UsersDelegate {

    private final UsersDAO usersDAO;
    private final ActionPipe<FetchUsersDataCommand> fetchUsersDataPipe;

    @Inject
    public UsersDelegate(UsersDAO usersDAO, Janet janet) {
        this.usersDAO = usersDAO;
        this.fetchUsersDataPipe = janet.createPipe(FetchUsersDataCommand.class, Schedulers.io());
    }

    public Observable<List<DataUser>> loadUsers(List<MessengerUser> messengerUsers) {
        return fetchUsersDataPipe.createObservableResult(FetchUsersDataCommand.from(messengerUsers))
                .map(Command::getResult);
    }

    public Observable<List<DataUser>> loadAndSaveUsers(List<MessengerUser> messengerUsers) {
        return loadUsers(messengerUsers)
                .doOnNext(usersDAO::save);
    }

    public Observable<List<DataUser>> loadMissingUsers(List<String> ids) {
        return usersDAO.getExitingUserByIds(ids)
                .take(1)
                .map(dataUsers -> obtainNotExistingUsers(dataUsers, ids))
                .flatMap(this::loadAndSaveUsers);
    }

    private List<MessengerUser> obtainNotExistingUsers(List<DataUser> existingUsers, List<String> userIds) {
        return Queryable.from(userIds)
                .filter(id -> {
                    for (DataUser existingUser : existingUsers) {
                        if (TextUtils.equals(existingUser.getId(), id)) return false;
                    }
                    return true;
                })
                .map(MessengerUser::new)
                .toList();
    }

}
