package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import org.immutables.value.Value;

@Value.Immutable
public interface LocationThrst extends HttpActionParams {

   String coordinates();
}
