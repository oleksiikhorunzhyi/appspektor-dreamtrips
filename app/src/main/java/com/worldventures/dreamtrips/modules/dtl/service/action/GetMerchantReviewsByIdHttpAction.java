package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantReviews;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/dtl/v2/merchants/{id}")
public class GetMerchantReviewsByIdHttpAction extends AuthorizedHttpAction {

   final @Path("id") String id;

   @Response MerchantReviews merchant;

   public GetMerchantReviewsByIdHttpAction(String id) {
      this.id = id;
   }

   public MerchantReviews merchant() {
      return merchant;
   }
}
