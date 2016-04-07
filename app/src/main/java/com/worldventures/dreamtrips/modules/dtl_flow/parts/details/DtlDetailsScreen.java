package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlDetailsScreen extends DtlScreen {

    void showEstimationDialog(PointsEstimationDialogBundle data);

    void openSuggestMerchant(MerchantIdBundle data);

    void openTransaction(DtlMerchant DtlMerchant, DtlTransaction dtlTransaction);

    void showSucceed(DtlMerchant DtlMerchant, DtlTransaction dtlTransaction);

    void openMap();

    void setTransaction(DtlTransaction dtlTransaction);

    void setSuggestMerchantButtonAvailable(boolean available);

    void share(DtlMerchant merchant);

    void locationResolutionRequired(Status status);

    void enableCheckinButton();

    void disableCheckinButton();

    void showMerchantMap(@Nullable Intent intent);

    void setMerchant(DtlMerchant merchant);
}
