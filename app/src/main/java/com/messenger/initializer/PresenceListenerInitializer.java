package com.messenger.initializer;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class PresenceListenerInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        messengerServerFacade.getGlobalEventEmitter().addPresenceListener((userId, isOnline) -> {
            User cachedUser = new Select()
                                .from(User.class)
                                .byIds(userId)
                                .querySingle();

            if (cachedUser == null) return;
            cachedUser.setOnline(isOnline);
            cachedUser.save();
        });
    }
}
