package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import org.immutables.value.Value;

@Value.Immutable
public interface ReviewsMerchantsActionParams extends HttpActionParams {

   String brandId();

   String productId();
}
