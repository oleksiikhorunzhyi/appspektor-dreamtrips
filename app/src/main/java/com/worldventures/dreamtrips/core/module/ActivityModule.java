package com.worldventures.dreamtrips.core.module;

import android.app.Activity;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class ActivityModule {

    protected BaseActivity baseActivity;

    public ActivityModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Provides
    public Activity provideActivity() {
        return baseActivity;
    }

    @Provides
    public ActivityRouter provideActivityCompass(FeatureManager featureManager) {
        return new ActivityRouter(baseActivity, featureManager);
    }

    @Provides
    public FragmentCompass provideFragmentCompass() {
        return new FragmentCompass(baseActivity, R.id.container_main);
    }

    @Provides
    @Named("details")
    public FragmentCompass provideFragmentCompassDetails() {
        return new FragmentCompass(baseActivity, R.id.detail_container);
    }

}
