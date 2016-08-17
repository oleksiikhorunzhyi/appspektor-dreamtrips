package com.worldventures.dreamtrips.modules.common;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.module.ApiModule;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.InterceptingOkClient;
import com.worldventures.dreamtrips.modules.feed.api.response.AllNotificationsCountResponseListener;
import com.worldventures.dreamtrips.modules.feed.api.response.HeaderChangedInformerListener;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                ApiModule.class
        },
        complete = false,
        library = true
)
public class ResponseSnifferModule {

    @Provides(type = Provides.Type.SET)
    InterceptingOkClient.ResponseHeaderListener provideNotificationCountListener(SnappyRepository db) {
        return new AllNotificationsCountResponseListener(db);
    }

    @Provides(type = Provides.Type.SET)
    InterceptingOkClient.ResponseHeaderListener provideHeaderChangedInformerResponseListener(NotificationCountEventDelegate notificationCountEventDelegate) {
        return new HeaderChangedInformerListener(notificationCountEventDelegate);
    }
}
