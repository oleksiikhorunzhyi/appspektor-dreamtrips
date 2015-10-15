package com.worldventures.dreamtrips.modules.common;

import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.module.ApiModule;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.InterceptingOkClient;
import com.worldventures.dreamtrips.modules.feed.api.response.HeaderChangedInformerListener;
import com.worldventures.dreamtrips.modules.feed.api.response.AllNotificationsCountResponseListener;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

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
    InterceptingOkClient.ResponseHeaderListener provideHeaderChangedInformerResponseListener(@Global EventBus eventBus) {
        return new HeaderChangedInformerListener(eventBus);
    }
}
