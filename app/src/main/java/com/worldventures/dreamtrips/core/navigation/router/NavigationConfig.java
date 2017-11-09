package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.AnimationConfig;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;

public class NavigationConfig {

   String key;
   NavigationType navigationType;
   Parcelable data;
   FragmentManager fragmentManager;
   ToolbarConfig toolbarConfig;
   @IdRes int containerId;
   Boolean backStackEnabled;
   Boolean clearBackStack = false;
   int gravity = 0x0000;
   int flags = -1;
   Fragment targetFragment;
   boolean manualOrientationActivity = false;

   Boolean transparentBackground = false;
   AnimationConfig animationConfig = null;

   NavigationConfig(NavigationType type) {
      navigationType = type;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public NavigationType getNavigationType() {
      return navigationType;
   }

   public Parcelable getData() {
      return data;
   }

   @Nullable
   public FragmentManager getFragmentManager() {
      return fragmentManager;
   }

   @Nullable
   public ToolbarConfig getToolbarConfig() {
      return toolbarConfig;
   }

   public int getContainerId() {
      return containerId;
   }

   public AnimationConfig getAnimationConfig() {
      return animationConfig;
   }

   public Boolean getTransparentBackground() {
      return transparentBackground;
   }

   public boolean isManualOrientationActivity() {
      return manualOrientationActivity;
   }

   public boolean isBackStackEnabled() {
      return backStackEnabled;
   }

   public Boolean getClearBackStack() {
      return clearBackStack;
   }

   public int getGravity() {
      return gravity;
   }

   public int getFlags() {
      return flags;
   }


   public Fragment getTargetFragment() {
      return targetFragment;
   }

   enum NavigationType {
      ACTIVITY, FRAGMENT, DIALOG, REMOVE
   }


}
