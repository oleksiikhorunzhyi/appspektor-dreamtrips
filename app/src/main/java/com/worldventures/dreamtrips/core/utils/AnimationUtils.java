package com.worldventures.dreamtrips.core.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationUtils {
   private AnimationUtils() {
   }

   public static void hideInTopEdge(View view) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         view.animate().translationY(-view.getHeight()).setDuration(200).withLayer();
      } else {
         view.animate().translationY(-view.getHeight()).setDuration(200);
      }
   }

   public static void appearFromTopEdge(View view) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         view.animate().translationY(0).setDuration(200).withLayer();
      } else {
         view.animate().translationY(0).setDuration(200);
      }
   }

   public static void hideInBottomEdge(View view) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         view.animate().translationY(view.getHeight()).setDuration(200).withLayer();
      } else {
         view.animate().translationY(view.getHeight()).setDuration(200);
      }
   }

   public static void appearFromBottomEdge(View view) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         view.animate().translationY(0).setDuration(200).withLayer();
      } else {
         view.animate().translationY(0).setDuration(200);
      }
   }

   public static void rotateByDegrees(View view, float degrees, int duration) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         view.animate().rotation(degrees).setDuration(duration).withLayer();
      } else {
         view.animate().rotation(degrees).setDuration(duration);
      }
   }

   public static Animation provideExpandAnimation(final View view, int duration) {
      return provideExpandAnimation(view, view.getMeasuredHeight(), duration);
   }

   public static Animation provideCollapseAnimation(final View view, int duration) {
      return provideCollapseAnimation(view, view.getMeasuredHeight(), duration);
   }

   public static Animation provideExpandAnimation(final View view, final int viewHeight, int duration) {
      view.setVisibility(View.VISIBLE);
      view.getLayoutParams().height = 0;
      //
      Animation animation = new Animation() {
         @Override
         protected void applyTransformation(float interpolatedTime, Transformation t) {
            view.getLayoutParams().height = (interpolatedTime == 1) ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (viewHeight * interpolatedTime);
            view.requestLayout();
         }
      };
      animation.setDuration(duration);
      return animation;
   }

   public static Animation provideCollapseAnimation(final View view, final int viewHeight, int duration) {
      Animation animation = new Animation() {
         @Override
         protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime == 1) view.setVisibility(View.GONE);
            else {
               view.getLayoutParams().height = viewHeight - (int) (viewHeight * interpolatedTime);
               view.requestLayout();
            }
         }
      };
      animation.setDuration(duration);
      return animation;
   }
}
