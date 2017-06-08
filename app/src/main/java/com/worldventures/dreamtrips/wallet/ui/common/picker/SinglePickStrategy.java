package com.worldventures.dreamtrips.wallet.ui.common.picker;

public class SinglePickStrategy implements WalletPickLimitStrategy {
   @Override
   public int pickLimit() {
      return 1;
   }
}
