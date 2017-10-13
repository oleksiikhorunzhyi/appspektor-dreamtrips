package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlTransactionPresenter extends DtlPresenter<DtlTransactionScreen, ViewState.EMPTY> {

   void onBackPressed();
}
