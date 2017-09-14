package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import com.worldventures.dreamtrips.api.dtl.merchants.requrest.Transaction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.views.TransactionView;

import java.util.List;

public interface DtlTransactionListScreen extends DtlScreen {

   void addTransactions(List<TransactionModel> transactions);

   void onRefreshSuccess();

   void onRefreshProgress();

   void onRefreshError(String error);

   void showEmpty(boolean isShow);

   void searchQuery(String query);

   TransactionView getRunnableView();

   void setAllTransactions(List<TransactionModel> transactions);

   void resetViewData();

}
