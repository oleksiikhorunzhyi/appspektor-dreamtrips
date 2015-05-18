package com.worldventures.dreamtrips.core.module;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.tripsimages.api.S3ImageUploader;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BaseArrayListAdapter.class,
                LoaderRecycleAdapter.class,
                IRoboSpiceAdapter.class,
                S3ImageUploader.class,
        },
        complete = false,
        library = true
)
public class ActivityModule {

    protected BaseActivity baseActivity;

    public ActivityModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Provides
    public ActivityRouter provideActivityCompass() {
        return new ActivityRouter(baseActivity);
    }

    @Provides
    public FragmentCompass provideFragmentCompass() {
        return new FragmentCompass(baseActivity, R.id.container);
    }

    @Provides
    @Named("details")
    public FragmentCompass provideFragmentCompassDetails() {
        return new FragmentCompass(baseActivity, R.id.detail_container);
    }

}
