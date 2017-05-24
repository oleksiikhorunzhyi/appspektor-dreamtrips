package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;

public class PhoneNumberCreator {

   @Nullable
   public static SmartCardUserPhone create(String code, String number) {
      if (ProjectTextUtils.isEmpty(code) || ProjectTextUtils.isEmpty(number)) {
         return null;
      } else {
         return SmartCardUserPhone.of(code, number);
      }
   }
}
