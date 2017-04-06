package com.worldventures.dreamtrips.api.dtl.merchants;


import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.Reviews;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/review/v1/reviews")
public class GetReviewsMerchantsHttpAction extends AuthorizedHttpAction {

   @Query("brandId") final String brandId;
   @Query("productId") final String productId;

   @Response Reviews response;

   public GetReviewsMerchantsHttpAction(String brandId, String productId) {
      this.brandId = brandId;
      this.productId = productId;
   }

   public Reviews response() {
      return response;
   }
}
