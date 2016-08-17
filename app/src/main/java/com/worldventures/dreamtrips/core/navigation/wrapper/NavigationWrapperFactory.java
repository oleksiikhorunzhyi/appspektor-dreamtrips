package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class NavigationWrapperFactory {

   public NavigationWrapper componentOrDialogNavigationWrapper(Router router, FragmentManager fragmentManager, Presenter.TabletAnalytic tabletAnalytic) {
      if (tabletAnalytic.isTabletLandscape()) {
         return new DialogNavigationWrapper(router, fragmentManager);
      } else {
         return new ComponentNavigationWrapper(router);
      }
   }
}
