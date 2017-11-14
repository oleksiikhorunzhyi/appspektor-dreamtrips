package com.worldventures.core.utils;

import android.support.annotation.Nullable;

public final class ThrowableUtils {

   private ThrowableUtils() {
   }

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
