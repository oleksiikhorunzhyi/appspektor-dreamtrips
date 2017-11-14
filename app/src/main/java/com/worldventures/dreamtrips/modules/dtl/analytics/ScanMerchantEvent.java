package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:QR Scan",
                trackers = AdobeTracker.TRACKER_KEY)
public class ScanMerchantEvent extends MerchantAnalyticsAction {

   @Attribute("scan") final String attribute = "1";

   @Attribute("scan_id") final String merchantToken;

   public ScanMerchantEvent(MerchantAttributes merchantAttributes, String merchantToken) {
      super(merchantAttributes);
      this.merchantToken = merchantToken;
   }
}
