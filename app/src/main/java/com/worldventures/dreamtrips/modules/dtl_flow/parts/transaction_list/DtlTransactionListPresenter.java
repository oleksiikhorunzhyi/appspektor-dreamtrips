package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_list;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlTransactionListPresenter extends DtlPresenter<DtlTransactionListScreen, ViewState.EMPTY> {

   void onBackPressed();
}
