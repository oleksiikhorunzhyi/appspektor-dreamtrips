package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

public interface DtlTransactionScreen extends DtlScreen {
      void showThrstTransaction(TransactionModel transactionModel, boolean isSuccessful);

      void showNonThrstTransaction(TransactionModel transactionModel);

      void showReceipt(String url);

      void showLoadingMerchantDialog();

      void hideLoadingMerchantDialog();

      void showCouldNotShowMerchantDialog();

}