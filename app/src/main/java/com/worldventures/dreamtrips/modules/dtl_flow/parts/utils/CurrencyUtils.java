package com.worldventures.dreamtrips.modules.dtl_flow.parts.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;

public final class CurrencyUtils {

   private CurrencyUtils() {
   }

   public static String toCurrency(double amount, String currencyCode, String currencySymbol) {
      NumberFormat format = NumberFormat.getCurrencyInstance();
      format.setMaximumFractionDigits(2);
      Currency currency = Currency.getInstance(currencyCode);
      format.setCurrency(currency);
      DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) format).getDecimalFormatSymbols();
      decimalFormatSymbols.setCurrencySymbol(currencySymbol);
      ((DecimalFormat) format).setDecimalFormatSymbols(decimalFormatSymbols);
      return format.format(amount);
   }

}
