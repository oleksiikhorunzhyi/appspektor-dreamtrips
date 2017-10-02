package com.worldventures.core.modules.picker.util.strategy;

public class SinglePhotoPickStrategy implements PhotoPickLimitStrategy {
   @Override
   public int photoPickLimit() {
      return 1;
   }
}
