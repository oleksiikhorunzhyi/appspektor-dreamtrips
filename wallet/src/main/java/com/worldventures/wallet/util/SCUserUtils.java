package com.worldventures.wallet.util;

import android.support.annotation.NonNull;

import com.worldventures.wallet.domain.entity.SmartCardUser;

public final class SCUserUtils {

   private SCUserUtils() {
   }

   public static String userFullName(@NonNull SmartCardUser user) {
      return userFullName(user.getFirstName(), user.getMiddleName(), user.getLastName());
   }

   public static String userFullName(@NonNull String firstName, @NonNull String middleName, @NonNull String lastName) {
      return firstName
            + (middleName.isEmpty() ? "" : " ") + middleName
            + (lastName.isEmpty() ? "" : " ") + lastName;
   }
}
