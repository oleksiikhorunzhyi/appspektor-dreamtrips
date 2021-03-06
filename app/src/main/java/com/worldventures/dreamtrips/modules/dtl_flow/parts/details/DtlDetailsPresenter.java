package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.worldventures.core.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;

public interface DtlDetailsPresenter extends DtlPresenter<DtlDetailsScreen, DtlMerchantDetailsState> {

   void trackPointEstimator();
   void trackSharing(@ShareType String type);
   void routeToMerchantRequested(@Nullable final Intent intent);
   void onCheckInClicked();
   void locationNotGranted();
   void onEstimationClick();
   void onMerchantClick();
   void onOfferClick(Offer offer);
   void onBackPressed();
   void showAllReviews();
   void addNewComments(Merchant merchant);
   void onClickRatingsReview(Merchant merchant);
   void onClickRateView();
   void setThrstFlow();
   void onClickPay();
   void orderFromMenu();
   void setupFullThrstBtn();
   void onShowHowPayVideo(String url);
   void onRemindLaterHowToPay();
   void onNeverShowHotToPay();
}
