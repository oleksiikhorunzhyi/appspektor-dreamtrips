package com.messenger.initializer;

import com.messenger.delegate.user.UserEventsDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class PresenceListenerInitializer implements AppInitializer {

   @Inject MessengerServerFacade messengerServerFacade;
   @Inject UserEventsDelegate userEventsDelegate;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      messengerServerFacade.getGlobalEventEmitter().addPresenceListener(userEventsDelegate::presenceChanged);
   }
}
