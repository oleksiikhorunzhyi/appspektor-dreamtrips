package com.worldventures.dreamtrips.core.navigation.wrapper;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class NavigationWrapperFactory {

    public NavigationWrapper componentOrDialogNavigationWrapper(ActivityRouter activityRouter, FragmentCompass fragmentCompass, Presenter.TabletAnalytic tabletAnalytic) {
        if (tabletAnalytic.isTabletLandscape()) {
            return new DialogNavigationWrapper(fragmentCompass.getFragmentManager());
        } else {
            return new ComponentNavigationWrapper(activityRouter);
        }
    }
}
