package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.util.ArrayList;
import java.util.List;

public interface DtlDetailsScreen extends DtlScreen {

   void showEstimationDialog(PointsEstimationDialogBundle data);

   void openSuggestMerchant(MerchantIdBundle data);

   void openTransaction(Merchant merchant, DtlTransaction dtlTransaction);

   void showSucceed(Merchant merchant, DtlTransaction dtlTransaction);

   void showThrstSucceed(Merchant merchant, String earnedPoints, String totalPoints);

   void setTransaction(DtlTransaction dtlTransaction, boolean isThrstTransaction);

   void setSuggestMerchantButtonAvailable(boolean available);

   void share(Merchant merchant);

   void locationResolutionRequired(Status status);

   void enableCheckinAndPayButtons();

   void disableCheckinAndPayButtons();

   void showMerchantMap(@Nullable Intent intent);

   void setMerchant(Merchant merchant);

   void setupMap();

   void expandOffers(List<String> positions);

   void expandHoursView();

   List<String> getExpandedOffersIds();

   boolean isHoursViewExpanded();

   void showAllReviews();

   void hideReviewViewsOnTablets();

   void addNoCommentsAndReviews();

   void addCommentsAndReviews(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews);

   void setTextRateAndReviewButton(int size);

   void userHasPendingReview();

   void showThrstFlowButton();

   void showEarnFlowButton();

   Activity getActivity();

   void showOrderFromMenu();

   void hideOrderFromMenu();

   void hideEarnFlowButton();

   boolean isTablet();

   void showHowToPayHint(String url);

   void openHowToPayVideo(String url);

   void hideVideoIfNeeded();
}
