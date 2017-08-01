package com.worldventures.dreamtrips.core.navigation;


import android.support.annotation.AnimRes;

import java.io.Serializable;

public class AnimationConfig implements Serializable {

   private final @AnimRes int animationEnter;
   private final @AnimRes int animationExit;

   public AnimationConfig(int animationEnter, int animationExit) {
      this.animationEnter = animationEnter;
      this.animationExit = animationExit;
   }

   public int getAnimationEnter() {
      return animationEnter;
   }

   public int getAnimationExit() {
      return animationExit;
   }
}
