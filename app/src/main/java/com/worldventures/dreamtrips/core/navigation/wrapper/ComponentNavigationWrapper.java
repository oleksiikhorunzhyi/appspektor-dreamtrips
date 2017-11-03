package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;
import android.support.v4.app.Fragment;


import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;

public class ComponentNavigationWrapper extends NavigationWrapper {

   public ComponentNavigationWrapper(Router router) {
      super(router);
   }

   @Override
   public void navigate(Class<? extends Fragment> clazz, Parcelable bundle) {
      router.moveTo(clazz, NavigationConfigBuilder.forActivity().data(bundle).build());
   }
}
