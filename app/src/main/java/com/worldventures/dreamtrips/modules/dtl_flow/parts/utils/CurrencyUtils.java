package com.worldventures.dreamtrips.modules.dtl_flow.parts.utils;

import java.text.NumberFormat;

public final class CurrencyUtils {

   private CurrencyUtils() {
   }

   public static String toCurrency(double amount) {
      NumberFormat format = NumberFormat.getCurrencyInstance();
      return format.format(amount);
   }

}
