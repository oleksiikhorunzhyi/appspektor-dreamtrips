package com.worldventures.core.janet;


import com.worldventures.janet.injection.ActionServiceLogger;

import timber.log.Timber;

public class ActionServiceLoggerImpl implements ActionServiceLogger {

   @Override
   public void error(Throwable throwable, String message, Object...args) {
      Timber.e(throwable, message, args);
   }
}
