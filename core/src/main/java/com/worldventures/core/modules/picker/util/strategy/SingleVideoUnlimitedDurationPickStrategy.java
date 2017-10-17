package com.worldventures.core.modules.picker.util.strategy;


public class SingleVideoUnlimitedDurationPickStrategy implements VideoPickLimitStrategy {
   @Override
   public int videoPickLimit() {
      return 1;
   }

   @Override
   public int videoDurationLimit() {
      return 0;
   }
}
