package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

import org.immutables.value.Value;

@Value.Immutable
public interface MerchantsActionParams extends HttpActionParams {

   FilterData filterData();

   DtlLocation location();

   RequestSourceType requestSource();
}
