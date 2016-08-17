package com.worldventures.dreamtrips.core.navigation.router;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;

public class RemovingTransactionConfigBuilder extends NavigationConfigBuilder {

   protected RemovingTransactionConfigBuilder() {
      super(NavigationConfig.NavigationType.REMOVE);
   }

   /**
    * Default config includes no specific setup when routing to activity
    */
   @Override
   public NavigationConfigBuilder useDefaults() {
      return this;
   }

   public RemovingTransactionConfigBuilder containerId(@IdRes int id) {
      navigationConfig.containerId = id;
      return this;
   }

   public RemovingTransactionConfigBuilder fragmentManager(FragmentManager fragmentManager) {
      navigationConfig.fragmentManager = fragmentManager;
      return this;
   }

   @Override
   protected void validateConfig() throws IllegalStateException {
      StringBuilder reasonBuilder = new StringBuilder("Navigation config corrupted state:\n");
      boolean corrupted = false;
      if (navigationConfig.fragmentManager == null) {
         reasonBuilder.append("fragmentManager is null\n");
         corrupted = true;
      }
      if (corrupted) throw new IllegalStateException(reasonBuilder.toString());
   }
}
