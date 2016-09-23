package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

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
   public static class TransactionSuccessShareEvent extends BaseDtlShareEvent {

      public TransactionSuccessShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
         super(merchantAttributes, sharingType);
      }
   }

   @AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Share",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class MerchantShareEvent extends BaseDtlShareEvent {

      public MerchantShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
         super(merchantAttributes, sharingType);
      }
   }

   public static class BaseDtlShareEvent extends MerchantAnalyticsAction {

      @Attribute("share") final String attribute = "1";

      @Attribute("share_id") final String sharingType;

      public BaseDtlShareEvent(MerchantAttributes merchantAttributes, @ShareType String sharingType) {
         super(merchantAttributes);
         this.sharingType = sharingType;
      }
   }
}
