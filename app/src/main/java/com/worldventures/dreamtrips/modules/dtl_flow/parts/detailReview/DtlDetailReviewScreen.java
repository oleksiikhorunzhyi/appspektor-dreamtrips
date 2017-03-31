package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlDetailReviewScreen extends DtlScreen {

    void finish();

    void onRefreshSuccess();

    void onRefreshProgress();

    void onRefreshError(String error);

    void showEmpty(boolean isShow);

    String getMerchantId();
}
