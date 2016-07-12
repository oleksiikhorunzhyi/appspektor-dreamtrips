package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;

public interface DtlDetailsPresenter extends DtlPresenter<DtlDetailsScreen, DtlMerchantDetailsState> {

    void trackPointEstimator();
    void trackSharing(@ShareType String type);
    void routeToMerchantRequested(@Nullable final Intent intent);
    void onCheckInClicked();
    void locationNotGranted();
    void onEstimationClick();
    void onMerchantClick();
    void onOfferClick(DtlOffer offer);
}
