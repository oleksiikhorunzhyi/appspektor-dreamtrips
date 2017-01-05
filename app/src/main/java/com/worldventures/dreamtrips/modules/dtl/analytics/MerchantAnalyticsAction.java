package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

import java.util.Locale;

public abstract class MerchantAnalyticsAction extends DtlAnalyticsAction {

   @Attribute("merchantname") final String merchantName;

   @Attribute("merchantID") final String merchantId;

   @Attribute("merchanttype") final String merchantType;

   @Attribute("partnerstatus") final String partnerStatus;

   public MerchantAnalyticsAction(MerchantAttributes merchantAttributes) {
      merchantName = merchantAttributes.displayName();
      merchantId = merchantAttributes.id();
      merchantType = merchantAttributes.type().toString().toLowerCase(Locale.US);
      partnerStatus = merchantAttributes.partnerStatus().toString().toLowerCase(Locale.US);
   }
}
