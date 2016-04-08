package com.worldventures.dreamtrips.modules.dtl.helper;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

public class DtlTransactionRouteCreator implements RouteCreator<DtlTransaction> {

    /**
     * Provides route based on current transaction state
     *
     * @param dtlTransaction current transaction object
     * @return Route based on transaction state
     */
    @Override
    public Route createRoute(DtlTransaction dtlTransaction) {
        if (dtlTransaction.getUploadTask() == null ||
                dtlTransaction.getBillTotal() == 0.0d) {
            return Route.DTL_SCAN_RECEIPT;
        } else if (!dtlTransaction.isVerified()) {
            return Route.DTL_VERIFY;
        } else if (!dtlTransaction.isMerchantCodeScanned() ||
                dtlTransaction.getDtlTransactionResult() == null) {
            return Route.DTL_SCAN_QR;
        } else {
            return Route.DTL_TRANSACTION_SUCCEED;
        }
    }
}
