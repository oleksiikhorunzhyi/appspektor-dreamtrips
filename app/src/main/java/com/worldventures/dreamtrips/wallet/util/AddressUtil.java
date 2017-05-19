package com.worldventures.dreamtrips.wallet.util;

import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class AddressUtil {

   public static String obtainAddressLabel(AddressInfo addressInfo) {
      Locale locale = LocaleHelper.getDefaultLocale();

      String address = concatAddress1AndAddress2(addressInfo, locale);
      StringBuilder builder = new StringBuilder();
      if (!isEmpty(address)) builder.append(address).append("\n");
      if (!isEmpty(addressInfo.city())) {
         builder.append(addressInfo.city()).append(", ");
      }
      if (!isEmpty(addressInfo.state())) {
         builder.append(addressInfo.state()).append(" ");
      }
      if (!isEmpty(addressInfo.zip())) {
         builder.append(addressInfo.zip());
      }
      // should be similar format "%s\n%s, %s %s"
      return builder.toString();
   }

   private static String concatAddress1AndAddress2(AddressInfo addressInfo, Locale locale) {
      if (isEmpty(addressInfo.address1())) return "";
      if (isEmpty(addressInfo.address2())) return addressInfo.address1();
      return String.format(locale, "%s\n%s", addressInfo.address1(), addressInfo.address2());
   }
}
