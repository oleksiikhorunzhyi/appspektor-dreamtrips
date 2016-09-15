package com.worldventures.dreamtrips.core.utils;

import android.support.annotation.StringRes;

public class QuantityHelper {

   public static
   @StringRes
   int chooseResource(int size, @StringRes int singleRes, @StringRes int multipleRes) {
      return size > 1 ? multipleRes : singleRes;
   }

   public static
   @StringRes
   int chooseResource(int size, @StringRes int zeroRes, @StringRes int singleRes, @StringRes int multipleRes) {
      return size == 0 ? zeroRes : chooseResource(size, singleRes, multipleRes);
   }
}
