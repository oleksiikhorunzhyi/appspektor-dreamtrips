package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityCompass;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.LaunchActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.LoginActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.LoginFragmentPresentation;
import com.worldventures.dreamtrips.view.presentation.MainActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.ProfileFragmentPresentation;
import com.worldventures.dreamtrips.view.presentation.WebViewFragmentPresentation;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        addsTo = DTModule.class,
        injects = {
                LaunchActivityPresentation.class,
                LoginActivityPresentation.class,
                LoginFragmentPresentation.class,
                WebViewFragmentPresentation.class,
                BaseActivityPresentation.class,
                ProfileFragmentPresentation.class,
                MainActivityPresentation.class},
        library = true
)
public class DomainModule {

    BaseActivity baseActivity;

    public DomainModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Provides
    public ActivityCompass provideActivityCompass() {
        return new ActivityCompass(baseActivity);
    }

    @Provides
    public FragmentCompass provideFragmentCompass() {
        return new FragmentCompass(baseActivity);
    }

    @Provides
    @Singleton
    public DataManager provideDataManager(DTApplication app) {
        return new DataManager(app);
    }

    @Provides
    @Singleton
    public SessionManager provideSessionManager(DTApplication app) {
        return new SessionManager(app);
    }
}
