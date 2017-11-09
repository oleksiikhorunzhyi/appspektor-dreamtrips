package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;

public class DialogNavigationWrapper extends NavigationWrapper {

   private final FragmentManager fragmentManager;

   public DialogNavigationWrapper(Router router, FragmentManager fragmentManager) {
      super(router);
      this.fragmentManager = fragmentManager;
   }

   @Override
   public void navigate(Class<? extends Fragment> clazz, Parcelable bundle) {
      router.moveTo(clazz, NavigationConfigBuilder.forDialog().fragmentManager(fragmentManager).data(bundle).build());
   }
}
