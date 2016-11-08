package com.worldventures.dreamtrips.wallet.util;

import com.worldventures.dreamtrips.BuildConfig;

public final class SmartCardFlavorUtil {

   private SmartCardFlavorUtil() {}

   public static boolean isSmartCardDevMockFlavor() {
      return BuildConfig.FLAVOR_stream.equals("smartcardmock") &&
            (BuildConfig.FLAVOR_build.equals("devpremarshmallow") || BuildConfig.FLAVOR_build.equals("dev"));
   }
}
