package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

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
