package com.worldventures.dreamtrips.api.dtl.merchants;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.Review;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ReviewParams;
import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/review/v1/reviews", method = HttpAction.Method.POST)
public class AddReviewHttpAction extends AuthorizedHttpAction {

   @Query("brandId") final String brandId;
   @Query("productId") final String productId;

   @Body
   ReviewParams requestBody;

   @Response
   Review response;

   public AddReviewHttpAction(String brandId, String productId, ReviewParams requestBody) {
      this.brandId = brandId;
      this.productId = productId;
      this.requestBody = requestBody;
   }

   public Review review() {
      return response;
   }
}
