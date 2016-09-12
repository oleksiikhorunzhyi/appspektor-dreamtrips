package com.worldventures.dreamtrips.modules.dtl.analytics;

import android.location.Location;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

import java.util.Locale;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Check-In",
                trackers = AdobeTracker.TRACKER_KEY)
public class CheckinEvent extends MerchantAnalyticsAction {

   @Attribute("checkin") final String attribute = "1";

   @Attribute("areperksavail") final String perksAvailable;

   @Attribute("arepointsavail") final String pointsAvailable;

   @Attribute("coordinates") final String coordinates;

   public CheckinEvent(Merchant merchant, Location location) {
      super(merchant);
      perksAvailable = MerchantHelper.merchantHasPerks(merchant) ? "Yes" : "No";
      pointsAvailable = MerchantHelper.merchantHasPoints(merchant) ? "Yes" : "No";
      coordinates = String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());
   }
}
