package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.AppNotificationImpl;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BaseArrayListAdapter.class,
                BaseDelegateAdapter.class,
                LoaderRecycleAdapter.class,
        },
        library = true, complete = false
)
public class UiBindingModule {

    @Provides
    public AppNotification provideInAppNotification(App app) {
        return new AppNotificationImpl(app);
    }

    @Singleton
    @Provides
    ActivityWatcher provideActivityWatcher(@ForApplication Context context, SessionHolder<UserSession> sessionHolder) {
        return new ActivityWatcher(context, sessionHolder);
    }

}
