package com.messenger.initializer;

import com.messenger.entities.User;
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
                        user.save();
                    });
        });

        emiter.addOnUserStatusChangedListener((userId, online) -> {
            User user = new User(userId);
            user.setOnline(online);
            //
            User cachedUser = new Select().from(User.class).byIds(userId).querySingle();
            if (cachedUser == null) user.save();
            else {
                cachedUser.setOnline(online);
                cachedUser.save();
            }
        });

        emiter.addOnFriendsChangedListener((userId, isFriend) -> usersDAO.markUserAsFriend(userId, isFriend));

}
}
