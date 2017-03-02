package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 5:Display Photo Has Been Set",
                trackers = AdobeTracker.TRACKER_KEY)
public class PhotoWasSetAction extends WalletAnalyticsAction {

   @Attribute("displayphotoset") final String displayPhotoSet = "1";
   @Attribute("photomethod") final String photoMethod = "Default";
   @Attribute("cardsetupstep5") final String cardSetupStep5 = "1";
}
