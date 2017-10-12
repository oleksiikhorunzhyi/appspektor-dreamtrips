package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 5:Display Photo Has Been Set",
                trackers = AdobeTracker.TRACKER_KEY)
public final class PhotoWasSetAction extends WalletAnalyticsAction {

   private static final String PHOTO_METHOD_DEFAULT = "Default";
   private static final String PHOTO_METHOD_NO_PHOTO = "No Photo";

   @Attribute("displayphotoset") final String displayPhotoSet;
   @Attribute("photomethod") final String photoMethod;
   @Attribute("cardsetupstep5") final String cardSetupStep5 = "1";

   private PhotoWasSetAction(String photoMethod, boolean photoAdded) {
      this.photoMethod = photoMethod;
      displayPhotoSet = photoAdded ? "1" : null;
   }

   public static PhotoWasSetAction methodDefault() {
      return new PhotoWasSetAction(PHOTO_METHOD_DEFAULT, true);
   }

   public static PhotoWasSetAction noPhoto() {
      return new PhotoWasSetAction(PHOTO_METHOD_NO_PHOTO, false);
   }
}
