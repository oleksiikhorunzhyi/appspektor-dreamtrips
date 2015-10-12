package com.worldventures.dreamtrips.core.module;

import android.app.Activity;

import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true, complete = false
)
public class UiUtilModule {

    @Provides
    Presenter.TabletAnalytic tabletAnalytic(Activity activity) {
        return () -> ViewUtils.isTablet(activity) && ViewUtils.isLandscapeOrientation(activity);
    }
}
