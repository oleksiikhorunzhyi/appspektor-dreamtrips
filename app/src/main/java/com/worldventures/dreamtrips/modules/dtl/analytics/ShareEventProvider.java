package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

public final class ShareEventProvider {

   private ShareEventProvider() {
   }

   public static TransactionSuccessShareEvent provideTransactionSuccessShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
      return new TransactionSuccessShareEvent(merchantAttributes, sharingType);
   }

   public static MerchantShareEvent provideMerchantShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
      return new MerchantShareEvent(merchantAttributes, sharingType);
   }

   @AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Congratulations:Share",
                   trackers = AdobeTracker.TRACKER_KEY)
   private static class TransactionSuccessShareEvent extends BaseDtlShareEvent {

      TransactionSuccessShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
         super(merchantAttributes, sharingType);
      }
   }

   @AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Share",
                   trackers = AdobeTracker.TRACKER_KEY)
   private static class MerchantShareEvent extends BaseDtlShareEvent {

      MerchantShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
         super(merchantAttributes, sharingType);
      }
   }

   private static abstract class BaseDtlShareEvent extends MerchantAnalyticsAction {

      @Attribute("share") final String attribute = "1";

      @Attribute("share_id") final String sharingType;

      BaseDtlShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
         super(merchantAttributes);
         this.sharingType = sharingType;
      }
   }
}
