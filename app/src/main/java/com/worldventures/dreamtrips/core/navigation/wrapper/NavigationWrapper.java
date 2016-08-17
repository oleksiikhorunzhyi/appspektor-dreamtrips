package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;

public abstract class NavigationWrapper {

   protected Router router;

   public NavigationWrapper(Router router) {
      this.router = router;
   }

   public abstract void navigate(Route route, Parcelable bundle);
}
