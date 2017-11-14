package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.worldventures.dreamtrips.core.navigation.AnimationConfig;

import timber.log.Timber;

class FragmentCompass {

   private final FragmentActivity activity;

   @IdRes private int containerId;
   private boolean backStackEnabled = true;
   private FragmentManager fragmentManager;
   private AnimationConfig animationConfig;

   /**
    * This constructor is to be used with {@link com.worldventures.dreamtrips.core.navigation.router.Router Router}
    * and {@link com.worldventures.dreamtrips.core.navigation.router.NavigationConfig NavigationConfig} only!
    */
   FragmentCompass(FragmentActivity activity) {
      this.activity = activity;
   }

   public void setContainerId(@IdRes int containerId) {
      this.containerId = containerId;
   }

   void setAnimationConfig(AnimationConfig animationConfig) {
      this.animationConfig = animationConfig;
   }

   public void replace(String backstackName, String clazzName, Bundle bundle, Fragment fragment) {
      action(backstackName, clazzName, bundle, fragment);
   }

   public void setFragmentManager(FragmentManager fragmentManager) {
      this.fragmentManager = fragmentManager;
   }

   protected void action(String backstackName, String clazzName, Bundle bundle, Fragment targetFragment) {
      if (!validateState()) {
         Timber.e(new IllegalStateException("Incorrect call of transaction manager action. validateState() false."), "");
      } else {
         Fragment fragment = Fragment.instantiate(activity, clazzName);
         setArgsToFragment(fragment, bundle);
         fragment.setTargetFragment(targetFragment, 0);
         FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         fragmentTransaction.replace(containerId, fragment, clazzName);
         if (backStackEnabled) {
            fragmentTransaction.addToBackStack(backstackName);
         }
         if (animationConfig != null) {
            fragmentTransaction.setCustomAnimations(animationConfig.getAnimationEnter(), animationConfig.getAnimationExit());
         }
         fragmentTransaction.commit();
      }
   }

   public void setBackStackEnabled(boolean enabled) {
      this.backStackEnabled = enabled;
   }

   private void setArgsToFragment(Fragment fragment, Bundle bundle) {
      fragment.setArguments(bundle);
   }

   private boolean validateState() {
      return activity != null && !activity.isFinishing();
   }
}
