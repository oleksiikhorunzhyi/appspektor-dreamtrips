package com.worldventures.dreamtrips.modules.feed.view.util;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.wrapper.ComponentNavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.DialogNavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class FeedDetailsNavigationWrapperFactory {

    public NavigationWrapper create(ActivityRouter activityRouter, FragmentCompass fragmentCompass, Presenter.TabletAnalytic tabletAnalytic, boolean isValidFeedObject) {
        if (tabletAnalytic.isTabletLandscape() && !isValidFeedObject) {
            return new DialogNavigationWrapper(fragmentCompass.getFragmentManager());
        } else {
            return new ComponentNavigationWrapper(activityRouter);
        }
    }
}
