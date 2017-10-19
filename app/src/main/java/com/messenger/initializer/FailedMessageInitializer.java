package com.messenger.initializer;


import com.messenger.delegate.MarkMessageDelegate;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;

import javax.inject.Inject;

public class FailedMessageInitializer implements AppInitializer {
   @Inject MarkMessageDelegate markMessageDelegate;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      markMessageDelegate.removeSendingMessagesMessage();
   }
}
