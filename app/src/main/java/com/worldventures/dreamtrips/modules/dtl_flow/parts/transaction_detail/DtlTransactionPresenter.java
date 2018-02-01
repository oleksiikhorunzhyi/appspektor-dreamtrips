package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import android.view.View;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlTransactionPresenter extends DtlPresenter<DtlTransactionScreen, ViewState.EMPTY> {

   void onSendEmailClick(View view);

   void showReceipt();

   void reviewMerchant();
}
