package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;

public abstract class NavigationConfigBuilder {

   protected final NavigationConfig navigationConfig;

   public static ActivityNavigationConfigBuilder forActivity() {
      return new ActivityNavigationConfigBuilder();
   }

   public static FragmentNavigationConfigBuilder forFragment() {
      return new FragmentNavigationConfigBuilder();
   }

   public static DialogNavigationConfigBuilder forDialog() {
      return new DialogNavigationConfigBuilder();
   }

   public static RemovingTransactionConfigBuilder forRemoval() {
      return new RemovingTransactionConfigBuilder();
   }

   protected NavigationConfigBuilder(NavigationConfig.NavigationType type) {
      navigationConfig = new NavigationConfig(type);
   }

   public NavigationConfigBuilder data(Parcelable data) {
      navigationConfig.data = data;
      return this;
   }

   /**
    * Create default builder and build it - for cases when we need no customization
    */
   public NavigationConfig buildDefault() {
      return useDefaults().build();
   }

   /**
    * Use some default config - this is specific for every navigation type and will be overridden
    */
   public abstract NavigationConfigBuilder useDefaults();

   public NavigationConfig build() {
      validateConfig();
      return navigationConfig;
   }

   protected abstract void validateConfig() throws IllegalStateException;
}
