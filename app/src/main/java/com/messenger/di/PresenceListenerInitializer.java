package com.messenger.di;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class PresenceListenerInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        messengerServerFacade.getGlobalEventEmitter().addPresenceListener(user -> {
            User cachedUser = new Select()
                                .from(User.class)
                                .byIds(user.getId())
                                .querySingle();

            if (cachedUser == null) return;
            cachedUser.setOnline(user.isOnline());
            ContentUtils.insert(User.CONTENT_URI, cachedUser);
        });
    }
}
