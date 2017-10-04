package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import org.immutables.value.Value;

@Value.Immutable
public interface TransactionThrstActionParams extends HttpActionParams {

   String merchantId();

   String transactionId();
}
