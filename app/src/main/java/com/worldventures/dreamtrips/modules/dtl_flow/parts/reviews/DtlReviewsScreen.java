package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import java.util.ArrayList;

public interface DtlReviewsScreen extends DtlScreen {

   void addCommentsAndReviews(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews);

   void onRefreshSuccess();

   void onRefreshProgress();

   void onRefreshError(String error);

   void showEmpty(boolean isShow);

   void showFrameLayoutReviews(boolean isShow);

   void userHasPendingReview();

   String getMerchantId();
}
