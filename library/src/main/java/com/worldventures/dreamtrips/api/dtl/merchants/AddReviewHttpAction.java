package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.CommentReview;
import com.worldventures.dreamtrips.api.dtl.merchants.model.Review;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ReviewParams;
import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;

@HttpAction(value = "api/review/v1/reviews", method = HttpAction.Method.POST)
public class AddReviewHttpAction extends AuthorizedHttpAction {

   @Query("brandId") final String brandId;
   @Query("productId") final String productId;

   @Body
   ReviewParams requestBody;

   @Response
   CommentReview response;

   public AddReviewHttpAction(RequestReviewParams requestReviewParams, ReviewParams requestBody) {
      this.brandId = requestReviewParams.brandId();
      this.productId = requestReviewParams.productId();
      this.requestBody = requestBody;
   }

   public CommentReview response() {
      return response;
   }
}
