package com.worldventures.dreamtrips.modules.picker.util.strategy;


public class AdjustableVideoPickStrategy implements VideoPickLimitStrategy {

   private final int videoPickLimit;
   private final int videoDurationLimit;

   public AdjustableVideoPickStrategy() {
      this.videoPickLimit = 0;
      this.videoDurationLimit = 0;
   }

   public AdjustableVideoPickStrategy(int videoPickLimit) {
      this.videoPickLimit = videoPickLimit;
      this.videoDurationLimit = 0;
   }

   public AdjustableVideoPickStrategy(int videoPickLimit, int videoDurationLimit) {
      this.videoPickLimit = videoPickLimit;
      this.videoDurationLimit = videoDurationLimit;
   }

   @Override
   public int videoPickLimit() {
      return videoPickLimit;
   }

   @Override
   public int videoDurationLimit() {
      return videoDurationLimit;
   }
}
