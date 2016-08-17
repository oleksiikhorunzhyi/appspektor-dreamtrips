package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

public class DialogNavigationConfigBuilder extends NavigationConfigBuilder {

   DialogNavigationConfigBuilder() {
      super(NavigationConfig.NavigationType.DIALOG);
   }

   /**
    * Default config includes no specific setup when routing a dialog
    */
   @Override
   public NavigationConfigBuilder useDefaults() {
      return this;
   }

   public DialogNavigationConfigBuilder data(Parcelable data) {
      super.data(data);
      return this;
   }

   public DialogNavigationConfigBuilder fragmentManager(FragmentManager fragmentManager) {
      navigationConfig.fragmentManager = fragmentManager;
      return this;
   }

   public DialogNavigationConfigBuilder gravity(int gravity) {
      navigationConfig.gravity = gravity;
      return this;
   }

   @Override
   protected void validateConfig() throws IllegalStateException {
      // so far dialog navigation has no specific state to validate
   }
}
