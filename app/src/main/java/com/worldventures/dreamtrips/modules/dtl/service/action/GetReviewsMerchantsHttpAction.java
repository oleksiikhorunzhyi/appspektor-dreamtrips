package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewsMerchant;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/review/v1/reviews")
public class GetReviewsMerchantsHttpAction extends AuthorizedHttpAction {

   @Query("brandId") final String brandId;
   @Query("productId") final String productId;

   @Response ReviewsMerchant reviewsMerchant;

   public GetReviewsMerchantsHttpAction(String brandId, String productId) {
      this.brandId = brandId;
      this.productId = productId;
   }

   public ReviewsMerchant reviews() {
      return reviewsMerchant;
   }
}
