package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import java.util.ArrayList;

/**
 * Created by yair.carreno on 2/1/2017.
 */

public interface DtlReviewsScreen extends DtlScreen {

   void addCommentsAndReviews(float ratingMerchant, int countReview, ArrayList<ReviewObject> listReviews);

}
