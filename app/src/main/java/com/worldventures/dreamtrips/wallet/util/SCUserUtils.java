package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

public class SCUserUtils {

   public static String userFullName(@NonNull SmartCardUser user) {
      return userFullName(user.firstName(), user.middleName(), user.lastName());
   }

   public static String userFullName(@NonNull String firstName, @NonNull String middleName, @NonNull String lastName) {
      return firstName +
                     (middleName.isEmpty() ? "" : " ") + middleName
                     + (lastName.isEmpty() ? "" : " ") + lastName;
   }
}
