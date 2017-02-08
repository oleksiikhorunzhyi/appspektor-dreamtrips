package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.modules.dtl.service.action.GetReviewsMerchantsHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.MerchantsActionParams;
import javax.inject.Inject;


public class ReviewsActionCreator implements HttpActionCreator<GetReviewsMerchantsHttpAction, MerchantsActionParams> {

   @Inject
   public ReviewsActionCreator(){}

   @Override
   public GetReviewsMerchantsHttpAction createAction(MerchantsActionParams params) {
      return new GetReviewsMerchantsHttpAction("1","634387ec-4b0a-4d72-b092-cafe8fe6f005");
   }
}
