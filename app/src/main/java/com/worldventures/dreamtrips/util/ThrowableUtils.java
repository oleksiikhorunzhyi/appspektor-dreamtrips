package com.worldventures.dreamtrips.util;

import android.support.annotation.Nullable;

public class ThrowableUtils {

   @Nullable
   public static <T> T getCauseByType(Class<T> causeType, Throwable exception) {
      if (causeType.isInstance(exception)) {
         return (T) exception;
      }
      if (exception != null && exception.getCause() != null) {
         return getCauseByType(causeType, exception.getCause());
      }
      return null;
   }

}
