package com.messenger.initializer;

import com.messenger.delegate.user.UserEventsDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;

import javax.inject.Inject;

public class RosterListenerInitializer implements AppInitializer {

   @Inject MessengerServerFacade messengerServerFacade;
   @Inject UserEventsDelegate userEventsDelegate;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      messengerServerFacade.getGlobalEventEmitter().addFriendAddedListener(userEventsDelegate::friendsAdded);
      messengerServerFacade.getGlobalEventEmitter().addFriendRemovedListener(userEventsDelegate::friendsRemoved);
   }

}
