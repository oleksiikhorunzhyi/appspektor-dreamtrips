package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlTransactionScreen extends DtlScreen {
      void showReceipt(String url);

      void showLoadingMerchantDialog();

      void hideLoadingMerchantDialog();

      void showCouldNotShowMerchantDialog();

}
