package com.worldventures.dreamtrips.modules.picker.view.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.modules.picker.util.MediaPickerStep;
import com.worldventures.dreamtrips.modules.picker.view.base.BaseMediaPickerLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class MediaPickerContainer extends FrameLayout {
   private AnimatorSet navigationAnimatorSet;
   private TreeMap<MediaPickerStep, BaseMediaPickerLayout> screens;
   private MediaPickerStep currentStep;

   public MediaPickerContainer(@NonNull Context context) {
      super(context);
   }

   public MediaPickerContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      addView(createDummyView(), 0);
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      removeViewAt(0);
   }

   public void setup(TreeMap<MediaPickerStep, BaseMediaPickerLayout> screens) {
      this.screens = screens;
      this.navigationAnimatorSet = new AnimatorSet();
      goNext();
   }

   public void goNext() {
      animateScreen(true);
   }

   public void goNext(Bundle args) {
      animateScreen(args, true);
   }

   public void goBack() {
      animateScreen(false);
   }

   private void animateScreen(boolean isForward) {
      animateScreen(null, isForward);
   }

   private void animateScreen(Bundle args, boolean isForward) {
      if (navigationAnimatorSet.isStarted() || navigationAnimatorSet.isRunning()) return;

      final List<Animator> animators = new ArrayList<>();
      final int deltaX = isForward ? -getWidth() : getWidth();

      final BaseMediaPickerLayout fadeInScreen = currentStep == null
            ? screens.get(screens.firstKey())
            : getFadeInScreenCandidate(isForward);
      if (fadeInScreen != null) {
         if (args != null) {
            fadeInScreen.setArguments(args);
         }
         fadeInScreen.setX(isForward ? getWidth() : -getWidth());
         addView(fadeInScreen);
         animators.add(ObjectAnimator.ofFloat(fadeInScreen, "x", fadeInScreen.getX(), fadeInScreen.getX() + deltaX));
      }
      final BaseMediaPickerLayout fadeOutScreen = currentStep == null
            ? null
            : screens.get(currentStep);
      if (fadeOutScreen != null) {
         animators.add(ObjectAnimator.ofFloat(fadeOutScreen, "x", fadeOutScreen.getX(), fadeOutScreen.getX() + deltaX));
      }
      navigationAnimatorSet.playTogether(animators);
      navigationAnimatorSet.addListener(new AnimatorListenerAdapter() {
         @Override
         public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            navigationAnimatorSet.removeListener(this);
            if (fadeOutScreen != null) {
               storeLayoutDimensionsForDummyView(fadeOutScreen.getWidth(), fadeOutScreen.getHeight());
               removeView(fadeOutScreen);
            }
            if (fadeInScreen != null) {
               currentStep = fadeInScreen.getStep();
            }
         }
      });
      navigationAnimatorSet.start();
   }

   private void storeLayoutDimensionsForDummyView(int width, int height) {
      final View dummyView = getChildAt(0);
      final ViewGroup.LayoutParams params = dummyView.getLayoutParams();
      params.width = width;
      params.height = height;
      dummyView.setLayoutParams(params);
   }

   private BaseMediaPickerLayout getFadeInScreenCandidate(boolean isForward) {
      BaseMediaPickerLayout fadeScreenCandidate = null;
      final MediaPickerStep rangeKey = isForward ? screens.lastKey() : screens.firstKey();
      if (currentStep != rangeKey) {
         fadeScreenCandidate = isForward ? screens.higherEntry(currentStep).getValue()
               : screens.lowerEntry(currentStep).getValue();
      }
      return fadeScreenCandidate;
   }

   public boolean canGoBack() {
      return currentStep != screens.firstKey();
   }

   public TreeMap<MediaPickerStep, BaseMediaPickerLayout> getScreens() {
      return screens;
   }

   private View createDummyView() {
      final View dummyView = new View(getContext());
      dummyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      dummyView.setBackgroundColor(Color.TRANSPARENT);
      return dummyView;
   }

   public void reset() {
      navigationAnimatorSet.cancel();
      navigationAnimatorSet = null;
      screens.clear();
      currentStep = null;
      removeAllViews();
   }
}
