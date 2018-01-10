package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import android.content.Context;

import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.NetworkUtils;

import static com.iovation.mobile.android.DevicePrint.getBlackbox;

public class UserReviewInfoProviderImpl implements UserReviewInfoProvider {

   private final Context context;

   public UserReviewInfoProviderImpl(Context context) {
      this.context = context;
   }

   @Override
   public String brandId() {
      return "1";
   }

   @Override
   public String fingerprint() {
      return getBlackbox(context.getApplicationContext());
   }

   @Override
   public String ipAddress() {
      return NetworkUtils.getIpAddress(true);
   }
}
