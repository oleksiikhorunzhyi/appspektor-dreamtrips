package com.worldventures.dreamtrips.wallet.util;

import io.techery.janet.smartcard.logging.Logger;
import timber.log.Timber;

public final class TimberLogger extends Logger {

   public TimberLogger(String tag) {
      super(tag);
   }

   @Override
   protected void log(String tag, String message) {
      Timber.tag(tag).d(message);
   }
}
