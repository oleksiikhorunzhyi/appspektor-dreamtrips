package com.worldventures.dreamtrips.wallet.util;


import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;

import java.util.Locale;

public class AddressUtil {

   public static String obtainAddressLabel(AddressInfoWithLocale addressInfoWithLocale) {
      AddressInfo addressInfo = addressInfoWithLocale.addressInfo();
      Locale locale = addressInfoWithLocale.locale();

      return String.format("%s\n%s\n%s, %s %s\n%s", addressInfo.address1(), addressInfo.address2(), addressInfo.city(), addressInfo
            .state(), addressInfo.zip(), locale.getISO3Country());
   }

}
