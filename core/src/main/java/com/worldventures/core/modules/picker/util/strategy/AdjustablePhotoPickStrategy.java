package com.worldventures.core.modules.picker.util.strategy;


public class AdjustablePhotoPickStrategy implements PhotoPickLimitStrategy {

   private final int photoPickLimit;

   public AdjustablePhotoPickStrategy() {
      this(Integer.MAX_VALUE);
   }

   public AdjustablePhotoPickStrategy(int photoPickLimit) {
      this.photoPickLimit = photoPickLimit;
   }

   @Override
   public int photoPickLimit() {
      return photoPickLimit;
   }
}
