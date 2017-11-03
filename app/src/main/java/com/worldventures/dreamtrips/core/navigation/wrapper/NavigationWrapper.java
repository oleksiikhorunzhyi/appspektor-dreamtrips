package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.core.navigation.router.Router;

public abstract class NavigationWrapper {

   protected Router router;

   public NavigationWrapper(Router router) {
      this.router = router;
   }

   public abstract void navigate(Class<? extends Fragment> clazz, Parcelable bundle);
}
