package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.detailReview.DtlDetailReviewScreen;

public interface DtlPaymentPresenter extends DtlPresenter<DtlPaymentScreen, ViewState.EMPTY> {

    void onBackPressed();

}
