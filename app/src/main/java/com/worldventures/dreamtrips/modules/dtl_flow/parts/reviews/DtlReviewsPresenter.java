package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

/**
 * Created by yair.carreno on 2/1/2017.
 */

public interface DtlReviewsPresenter extends DtlPresenter<DtlReviewsScreen, ViewState.EMPTY> {

   void onBackPressed();
}
