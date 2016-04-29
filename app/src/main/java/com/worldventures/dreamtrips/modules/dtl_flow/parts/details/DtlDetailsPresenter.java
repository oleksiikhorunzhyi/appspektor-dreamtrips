package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlDetailsPresenter extends DtlPresenter<DtlDetailsScreen, ViewState.EMPTY> {

    void trackScreen();
    void trackPointEstimator();
    void trackSharing(@ShareType String type);
    void routeToMerchantRequested(@Nullable final Intent intent);
    void onCheckInClicked();
    void onEstimationClick();
    void onMerchantClick();
    void onOfferClick(DtlOfferData offer);
}
