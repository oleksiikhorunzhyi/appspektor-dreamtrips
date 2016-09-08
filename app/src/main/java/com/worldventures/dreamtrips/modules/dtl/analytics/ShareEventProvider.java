package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public final class ShareEventProvider {

   private ShareEventProvider() {
   }

   public static TransactionSuccessShareEvent provideTransactionSuccessShareEvent(Merchant merchant, @ShareType String sharingType) {
      return new TransactionSuccessShareEvent(merchant, sharingType);
   }

   public static MerchantShareEvent provideMerchantShareEvent(Merchant merchant, @ShareType String sharingType) {
      return new MerchantShareEvent(merchant, sharingType);
   }

   @AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Congratulations:Share",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class TransactionSuccessShareEvent extends BaseDtlShareEvent {

      public TransactionSuccessShareEvent(Merchant merchant, @ShareType String sharingType) {
         super(merchant, sharingType);
      }
   }

   @AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Share",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class MerchantShareEvent extends BaseDtlShareEvent {

      public MerchantShareEvent(Merchant merchant, @ShareType String sharingType) {
         super(merchant, sharingType);
      }
   }

   public static class BaseDtlShareEvent extends MerchantAnalyticsAction {

      @Attribute("share") final String attribute = "1";

      @Attribute("share_id") final String sharingType;

      public BaseDtlShareEvent(Merchant merchant, @ShareType String sharingType) {
         super(merchant);
         this.sharingType = sharingType;
      }
   }
}
