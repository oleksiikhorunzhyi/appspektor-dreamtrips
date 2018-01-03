package com.worldventures.dreamtrips.modules.dtl_flow.parts.detail_review; //NOPMD TODO: Resolve naming

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlDetailReviewScreen extends DtlScreen {

   void enableInputs();

   void disableInputs();

   void finish();

   void onRefreshSuccess();

   void onRefreshProgress();

   void onRefreshError(String error);

   void showEmpty(boolean isShow);

   String getMerchantId();

   boolean isFromListReview();

   void showFlaggingSuccess();

   void showFlaggingError();
}
