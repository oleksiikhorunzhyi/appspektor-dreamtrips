package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import org.immutables.value.Value;

@Value.Immutable
public interface FullMerchantActionParams extends HttpActionParams {

   String merchantId();

   @Nullable String offerId();

   @Nullable DtlLocation location();

}
