package com.worldventures.dreamtrips.core.module;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true,
        includes = {
                UiUtilModule.class
        }
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
    public FragmentManager provideFragmentManager(){
        return  baseActivity.getSupportFragmentManager();
    }

}
