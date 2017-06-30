package com.worldventures.dreamtrips.wallet.ui.common.picker;

public class SinglePickStrategy implements WalletPickLimitStrategy {
   @Override
   public int photoPickLimit() {
      return 1;
   }

   @Override
   public int videoDurationLimit() {
      return 0;
   }
}
