package com.messenger.initializer;

import com.messenger.entities.DataUser;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.UsersDAO;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class PresenceListenerInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;

    @Inject
    UsersDAO usersDAO;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        GlobalEventEmitter emiter = messengerServerFacade.getGlobalEventEmitter();
        emiter.addPresenceListener((userId, isOnline) -> {
            usersDAO.getUserById(userId)
                    .first()
                    .subscribeOn(Schedulers.io())
                    .filter(user -> user != null)
                    .subscribe(user -> {
                        user.setOnline(isOnline);
                        usersDAO.save(user);
                    });
        });

        emiter.addOnUserStatusChangedListener((userId, online) -> {
            DataUser user = new DataUser(userId);
            user.setOnline(online);
            //
            DataUser cachedUser = UsersDAO.getUser(userId);
            if (cachedUser == null) usersDAO.save(user);
            else {
                cachedUser.setOnline(online);
                usersDAO.save(cachedUser);
            }
        });

        emiter.addOnFriendsChangedListener((userId, isFriend) -> usersDAO.markUserAsFriend(userId, isFriend));

}
}
