package com.messenger.initializer;


import com.messenger.delegate.MarkMessageDelegate;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class FailedMessageInitializer implements AppInitializer {
   @Inject MarkMessageDelegate markMessageDelegate;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      markMessageDelegate.removeSendingMessagesMessage();
   }
}
