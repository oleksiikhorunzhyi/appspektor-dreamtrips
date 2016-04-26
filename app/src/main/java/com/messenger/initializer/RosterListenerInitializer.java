package com.messenger.initializer;

import com.messenger.delegate.UserDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class RosterListenerInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    UserDelegate userDelegate;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        messengerServerFacade.getGlobalEventEmitter().addFriendAddedListener(userDelegate::friendsAdded);
        messengerServerFacade.getGlobalEventEmitter().addFriendRemovedListener(userDelegate::friendsRemoved);
    }

}
