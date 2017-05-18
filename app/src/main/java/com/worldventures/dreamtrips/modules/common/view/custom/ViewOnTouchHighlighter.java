package com.worldventures.dreamtrips.modules.common.view.custom;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

/**
 * Set this class as OnTouchListener for highlighting view on press
 *
 * @see android.view.View#setOnTouchListener(android.view.View.OnTouchListener)
 * You may customize:
 * - normal state view color
 * - press state view color
 * - color change duration
 * This class would not block MotionEvens. Feel free for extending.
 * <p>
 * ATENTION! RESTRICTION:
 * Colors should be in 'argb' format! 'rgb' will make view transparent on animation end
 */
public class ViewOnTouchHighlighter implements View.OnTouchListener {
   private int colorNormal;
   private int colorPress;
   private long duration;
   private ValueAnimator anim = new ValueAnimator();

   public ViewOnTouchHighlighter(int colorNormal, int colorPress, long duration) {
      this.colorNormal = colorNormal;
      this.colorPress = colorPress;
      this.duration = duration;
   }

   @Override
   public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()) {
         case MotionEvent.ACTION_DOWN:
            anim.setIntValues(colorNormal, colorPress);
            anim.setEvaluator(new ArgbEvaluator());
            anim.addUpdateListener(valueAnimator -> v.getBackground()
                  .setColorFilter((Integer) valueAnimator.getAnimatedValue(), PorterDuff.Mode.MULTIPLY));
            anim.setDuration(duration);
            anim.start();
            break;
         case MotionEvent.ACTION_UP:
            anim.cancel();
            v.getBackground().setColorFilter(null);
            break;
      }
      return false;
   }

   public static class Builder {
      private int colorNormal = 0xffffffff;
      private int colorPress = 0xff76b2fe;    //hardcoded values/color:accent
      private long duration = 150;

      public Builder setDefault() {
         return this;
      }

      public Builder setColorNormal(int colorNormal) {
         this.colorNormal = colorNormal;
         return this;
      }

      public Builder setColorPress(int colorPress) {
         this.colorPress = colorPress;
         return this;
      }

      public Builder setDuration(long duration) {
         this.duration = duration;
         return this;
      }

      public ViewOnTouchHighlighter build() {
         return new ViewOnTouchHighlighter(colorNormal, colorPress, duration);
      }
   }


}