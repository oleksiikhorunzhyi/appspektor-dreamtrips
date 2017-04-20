package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.AddFlaggingReviewAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableSdkFlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.FlaggingReviewActionParams;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.NetworkUtils;

import javax.inject.Inject;


public class FlaggingActionCreator implements HttpActionCreator<AddFlaggingReviewAction, FlaggingReviewActionParams> {

   @Inject
   public FlaggingActionCreator(){}

   @Override
   public AddFlaggingReviewAction createAction(FlaggingReviewActionParams params) {
      return new AddFlaggingReviewAction("", ImmutableSdkFlaggingReviewParams.builder()
                                          .authorIpAddress(NetworkUtils.getIpAddress(true))
                                          .contentType(1)
                                          .feedbackType(1)
                                          .build());
   }
}
