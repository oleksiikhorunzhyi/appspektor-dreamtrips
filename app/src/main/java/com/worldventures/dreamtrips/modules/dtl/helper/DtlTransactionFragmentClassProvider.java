package com.worldventures.dreamtrips.modules.dtl.helper;

import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanQrCodeFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlScanReceiptFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlTransactionSucceedFragment;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlVerifyAmountFragment;

public class DtlTransactionFragmentClassProvider implements FragmentClassProvider<DtlTransaction> {

   /**
    * Provides fragment class based on current transaction state
    *
    * @param dtlTransaction current transaction object
    * @return Fragment class based on transaction state
    */
   @Override
   public Class<? extends Fragment> provideFragmentClass(DtlTransaction dtlTransaction) {
      if (dtlTransaction.getUploadTask() == null || dtlTransaction.getBillTotal() == 0.0d) {
         return DtlScanReceiptFragment.class;
      } else if (!dtlTransaction.isVerified()) {
         return DtlVerifyAmountFragment.class;
      } else if (!dtlTransaction.isMerchantCodeScanned() || dtlTransaction.getDtlTransactionResult() == null) {
         return DtlScanQrCodeFragment.class;
      } else {
         return DtlTransactionSucceedFragment.class;
      }
   }
}
