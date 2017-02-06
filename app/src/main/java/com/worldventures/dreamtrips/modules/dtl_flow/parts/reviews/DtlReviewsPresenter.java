package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.util.ArrayList;

public interface DtlReviewsPresenter extends DtlPresenter<DtlReviewsScreen, ViewState.EMPTY> {

   void onBackPressed();
   void addNewComments(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews);
}
