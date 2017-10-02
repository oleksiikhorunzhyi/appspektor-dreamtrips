package com.worldventures.core.modules.picker.util.strategy;


public class SingleVideoLimitedDurationPickStrategy implements VideoPickLimitStrategy {

   private final int videoDurationLimit;

   public SingleVideoLimitedDurationPickStrategy(int videoDurationLimit) {
      this.videoDurationLimit = videoDurationLimit;
   }

   @Override
   public int videoPickLimit() {
      return 1;
   }

   @Override
   public int videoDurationLimit() {
      return videoDurationLimit;
   }
}
