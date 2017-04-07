package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;

public class ActivityNavigationConfigBuilder extends NavigationConfigBuilder {

   ActivityNavigationConfigBuilder() {
      super(NavigationConfig.NavigationType.ACTIVITY);
   }

   /**
    * Default config includes no specific setup when routing to activity
    */
   @Override
   public NavigationConfigBuilder useDefaults() {
      return this;
   }

   public ActivityNavigationConfigBuilder data(Parcelable data) {
      super.data(data);
      return this;
   }

   public ActivityNavigationConfigBuilder toolbarConfig(ToolbarConfig config) {
      navigationConfig.toolbarConfig = config;
      return this;
   }

   public ActivityNavigationConfigBuilder flags(int flags) {
      navigationConfig.flags = flags;
      return this;
   }

   public ActivityNavigationConfigBuilder manualComponentActivity(boolean manualComponentActivity) {
      navigationConfig.manualComponentActivity = manualComponentActivity;
      return this;
   }

   @Override
   protected void validateConfig() throws IllegalStateException {
      // so far activity navigation has no specific state to validate
   }
}
