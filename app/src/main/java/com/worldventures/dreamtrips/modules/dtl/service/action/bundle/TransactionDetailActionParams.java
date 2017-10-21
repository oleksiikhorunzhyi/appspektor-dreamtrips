package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import org.immutables.value.Value;

@Value.Immutable
public interface TransactionDetailActionParams extends HttpActionParams {
   String localeId();
   int take();
   int skip();
}
