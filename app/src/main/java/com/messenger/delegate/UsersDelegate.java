package com.messenger.delegate;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.UsersDAO;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class UsersDelegate {

    @Inject
    UsersDAO usersDAO;
    @Inject
    UserProcessor userProcessor;

    @Inject
    public UsersDelegate(UsersDAO usersDAO, UserProcessor userProcessor) {
        this.usersDAO = usersDAO;
        this.userProcessor = userProcessor;
    }

    public Observable<List<DataUser>> loadIfNeedUsers(List<String> ids) {
        Observable<List<MessengerUser>> messengerUserObservable = usersDAO.getExitingUserByIds(ids)
                .take(1)
                .map(dataUsers -> obtainNotExistingUsers(dataUsers, ids));

        return userProcessor.connectToUserProvider(messengerUserObservable);
    }

    private List<MessengerUser> obtainNotExistingUsers(List<DataUser> existingUsers, List<String> userIds){
        return Queryable.from(userIds)
                .filter(id -> {
                    for (DataUser existingUser: existingUsers){
                        if (TextUtils.equals(existingUser.getId(), id)) return false;
                    }
                    return true;
                })
                .map(MessengerUser::new)
                .toList();
    }

}
