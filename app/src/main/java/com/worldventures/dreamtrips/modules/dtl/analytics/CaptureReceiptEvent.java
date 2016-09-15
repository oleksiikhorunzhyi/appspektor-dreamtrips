package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Receipt Capture",
                trackers = AdobeTracker.TRACKER_KEY)
public class CaptureReceiptEvent extends MerchantAnalyticsAction {

   @Attribute("capture") final String attribute = "1";

   public CaptureReceiptEvent(DtlMerchant dtlMerchant) {
      super(dtlMerchant);
   }
}
