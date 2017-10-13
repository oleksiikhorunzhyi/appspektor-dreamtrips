package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlTransactionListPresenter extends DtlPresenter<DtlTransactionListScreen, ViewState.EMPTY> {

   void onBackPressed();

   void loadFirstPage();

   void getAllTransactionsToQuery(String query);

   void addMoreTransactions(int indexOf);

}
