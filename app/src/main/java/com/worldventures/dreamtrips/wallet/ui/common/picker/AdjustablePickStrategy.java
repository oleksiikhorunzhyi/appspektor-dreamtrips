package com.worldventures.dreamtrips.wallet.ui.common.picker;


public class AdjustablePickStrategy implements WalletPickLimitStrategy {

   private final int photoPickLimit;
   private final int videoDurationLimit;

   public AdjustablePickStrategy() {
      this(Integer.MAX_VALUE, 0);
   }

   public AdjustablePickStrategy(int photoPickLimit, int videoDurationLimit) {
      this.photoPickLimit = photoPickLimit;
      this.videoDurationLimit = videoDurationLimit;
   }

   @Override
   public int photoPickLimit() {
      return photoPickLimit;
   }

   @Override
   public int videoDurationLimit() {
      return videoDurationLimit;
   }
}
