package com.worldventures.core.janet;


import com.worldventures.dreamtrips.mobilesdk.service.ServiceLabel;

public class BaseThirdPartyHttpAction implements ServiceLabel {
   public static final String NON_API_ACTION = "non-api-action";

   @Override
   public String label() {
      return NON_API_ACTION;
   }
}
