package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlMapInfoScreen extends DtlScreen {

   void visibleLayout(boolean show);

   void setMerchant(ThinMerchant merchant);

   void showPendingReviewError();
}
