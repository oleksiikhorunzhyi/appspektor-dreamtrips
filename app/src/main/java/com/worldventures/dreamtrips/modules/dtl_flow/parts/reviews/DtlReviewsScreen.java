package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views.OfferWithReviewView;

import java.util.ArrayList;
import java.util.List;

public interface DtlReviewsScreen extends DtlScreen {

   void addCommentsAndReviews(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews);

   void onRefreshSuccess();

   void onRefreshProgress();

   void onRefreshError(String error);

   void showEmpty(boolean isShow);

   void showFrameLayoutReviews(boolean isShow);

   void showRefreshProgress(boolean isShow);

   void userHasPendingReview();

   void setEventListener(OfferWithReviewView.IMyEventListener listener);

   String getMerchantId();

   List<ReviewObject> getCurrentReviews();

   void resetViewData();

   void setContainerDetail(OfferWithReviewView mContainerDetail);
}
