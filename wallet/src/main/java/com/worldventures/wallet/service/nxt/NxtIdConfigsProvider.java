package com.worldventures.wallet.service.nxt;

public interface NxtIdConfigsProvider {

   String nxtidApi();

   long apiTimeoutSec();

   String nxtidSessionApi();

   String apiVersion();
}
