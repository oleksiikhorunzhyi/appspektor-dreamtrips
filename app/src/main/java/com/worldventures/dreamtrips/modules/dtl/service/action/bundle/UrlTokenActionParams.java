package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import com.worldventures.dreamtrips.api.dtl.merchants.requrest.Location;

import org.immutables.value.Value;

@Value.Immutable
public interface UrlTokenActionParams extends HttpActionParams {

   String checkinTime();

   String currencyCode();

   String receiptPhotoUrl();

   Location location();

   String merchantId();
}
