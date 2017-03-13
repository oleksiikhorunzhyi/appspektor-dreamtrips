package com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlDetailReviewPresenter extends DtlPresenter<DtlDetailReviewScreen, ViewState.EMPTY> {

    void onBackPressed();
}
