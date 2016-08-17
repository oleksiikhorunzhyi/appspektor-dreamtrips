package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;

public class ComponentNavigationWrapper extends NavigationWrapper {

   public ComponentNavigationWrapper(Router router) {
      super(router);
   }

   @Override
   public void navigate(Route route, Parcelable bundle) {
      router.moveTo(route, NavigationConfigBuilder.forActivity().data(bundle).build());
   }
}
