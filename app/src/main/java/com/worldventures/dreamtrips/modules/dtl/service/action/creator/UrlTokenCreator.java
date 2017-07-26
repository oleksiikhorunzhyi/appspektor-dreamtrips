package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.GetUrlTokenPilotAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableGetUrlTokenParamsSdk;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableLocationThrst;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.UrlTokenActionParams;

import javax.inject.Inject;


public class UrlTokenCreator implements HttpActionCreator<GetUrlTokenPilotAction, UrlTokenActionParams> {

   @Inject
   public UrlTokenCreator(){}

   @Override
   public GetUrlTokenPilotAction createAction(UrlTokenActionParams params) {
      return new GetUrlTokenPilotAction(params.merchantId(), ImmutableGetUrlTokenParamsSdk.builder()
                                                .checkinTime(params.checkinTime())
                                                .receiptPhotoUrl(params.receiptPhotoUrl())
                                                .currencyCode(params.currencyCode())
                                                .location(ImmutableLocationThrst.builder()
                                                      .coordinates(params.location().coordinates())
                                                      .build())
                                             .build());
   }
}
