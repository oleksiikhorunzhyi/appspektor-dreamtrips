package com.worldventures.dreamtrips.wallet.ui.common.picker;


public class AdjustablePickStrategy implements WalletPickLimitStrategy {

   private final int pickLimit;

   public AdjustablePickStrategy() {
      this.pickLimit = Integer.MAX_VALUE;
   }

   public AdjustablePickStrategy(int pickLimit) {
      this.pickLimit = pickLimit;
   }

   @Override
   public int pickLimit() {
      return pickLimit;
   }
}
