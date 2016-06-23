package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public final class ShareEventProvider {

    private ShareEventProvider() {
    }

    public static TransactionSuccessShareEvent provideTransactionSuccessShareEvent(
            DtlMerchant dtlMerchant, @ShareType String sharingType) {
        return new TransactionSuccessShareEvent(dtlMerchant, sharingType);
    }

    public static MerchantShareEvent provideMerchantShareEvent(DtlMerchant dtlMerchant,
                                                               @ShareType String sharingType) {
        return new MerchantShareEvent(dtlMerchant, sharingType);
    }

    @Analytics(action = "local:Restaurant-Listings:Merchant View:Congratulations:Share",
            trackers = AdobeTracker.TRACKER_KEY)
    public static class TransactionSuccessShareEvent extends BaseDtlShareEvent {

        public TransactionSuccessShareEvent(DtlMerchant dtlMerchant, @ShareType String sharingType) {
            super(dtlMerchant, sharingType);
        }
    }

    @Analytics(action = "local:Restaurant-Listings:Merchant View:Share",
            trackers = AdobeTracker.TRACKER_KEY)
    public static class MerchantShareEvent extends BaseDtlShareEvent {

        public MerchantShareEvent(DtlMerchant dtlMerchant, @ShareType String sharingType) {
            super(dtlMerchant, sharingType);
        }
    }

    public static class BaseDtlShareEvent extends DtlAnalyticsAction {

        @Attribute("share")
        final String attribute = "1";

        @Attribute("share_id")
        final String sharingType;

        @Attribute("merchantname")
        final String merchantName;

        @Attribute("merchantID")
        final String merchantId;

        public BaseDtlShareEvent(DtlMerchant dtlMerchant, @ShareType String sharingType) {
            this.sharingType = sharingType;
            merchantName = dtlMerchant.getDisplayName();
            merchantId = dtlMerchant.getId();
        }
    }
}
