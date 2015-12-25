package com.worldventures.dreamtrips.core.module;

import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.AppNotificationImpl;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.worldventures.dreamtrips.App;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BaseArrayListAdapter.class,
                LoaderRecycleAdapter.class,
        },
        library = true, complete = false
)
public class UiBindingModule {

        @Provides
        public AppNotification provideInAppNotification(App app) {
                return new AppNotificationImpl(app);
        }

}
