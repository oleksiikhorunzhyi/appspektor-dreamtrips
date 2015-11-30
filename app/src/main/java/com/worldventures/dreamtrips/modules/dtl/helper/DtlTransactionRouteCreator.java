package com.worldventures.dreamtrips.modules.dtl.helper;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;

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
                dtlTransaction.getAmount() == 0.0d) {
            return Route.DTL_SCAN_RECEIPT;
        } else if (TextUtils.isEmpty(dtlTransaction.getCode()) ||
                dtlTransaction.getDtlTransactionResult() == null) {
            return Route.DTL_VERIFY;
        } else {
            return Route.DTL_TRANSACTION_SUCCEED;
        }
    }
}