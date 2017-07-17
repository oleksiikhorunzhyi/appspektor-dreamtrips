package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import com.worldventures.dreamtrips.api.dtl.merchants.GetUrlTokenPilotAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableGetUrlTokenParamsSdk;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.UrlTokenActionParams;

import javax.inject.Inject;


public class UrlTokenCreator implements HttpActionCreator<GetUrlTokenPilotAction, UrlTokenActionParams> {

   @Inject
   public UrlTokenCreator(){}

   @Override
   public GetUrlTokenPilotAction createAction(UrlTokenActionParams params) {
      return new GetUrlTokenPilotAction("fcedee48-667e-482b-8f31-a47335b7ed6c", ImmutableGetUrlTokenParamsSdk.builder()
                                                .checkinTime(params.checkinTime())
                                                .receiptPhotoUrl(params.receiptPhotoUrl())
                                                .currencyCode(params.currencyCode())
                                                .location(params.location())
                                             .build());
   }
}
