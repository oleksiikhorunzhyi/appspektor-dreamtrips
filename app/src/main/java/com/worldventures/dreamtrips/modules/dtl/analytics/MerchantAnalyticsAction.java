package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

import java.util.Locale;

public abstract class MerchantAnalyticsAction extends DtlAnalyticsAction {

   @Attribute("merchantname") final String merchantName;

   @Attribute("merchantID") final String merchantId;

   @Attribute("merchanttype") final String merchantType;

   @Attribute("partnerstatus") final String partnerStatus;

   public MerchantAnalyticsAction(MerchantAttributes merchantAttributes) {
      merchantName = merchantAttributes.displayName();
      merchantId = merchantAttributes.id();
      merchantType = merchantAttributes.type().toString().toLowerCase(Locale.US);
      partnerStatus = merchantAttributes.partnerStatus().toString().toLowerCase(Locale.US);
   }

   public MerchantAnalyticsAction(DtlMerchant merchant) {
      merchantName = merchant.getDisplayName();
      merchantId = merchant.getId();
      merchantType = merchant.getType().toString().toLowerCase(Locale.US);
      partnerStatus = merchant.getPartnerStatus().toString().toLowerCase(Locale.US);
   }
}
