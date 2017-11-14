package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.GetReviewsMerchantsHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ReviewsMerchantsActionParams;

import javax.inject.Inject;


public class ReviewsActionCreator implements HttpActionCreator<GetReviewsMerchantsHttpAction, ReviewsMerchantsActionParams> {

   @Inject
   public ReviewsActionCreator() {
      //do nothing
   }

   @Override
   public GetReviewsMerchantsHttpAction createAction(ReviewsMerchantsActionParams params) {
      return new GetReviewsMerchantsHttpAction(params.brandId(), params.productId(), params.indexOf(), params.limit());
   }
}
