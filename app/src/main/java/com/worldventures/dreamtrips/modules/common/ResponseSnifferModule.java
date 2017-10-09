package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.core.janet.api_lib.ResponseListener;
import com.worldventures.dreamtrips.modules.common.delegate.HttpResponseSnifferDelegate;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.social.ui.feed.api.response.RequestCountResponseListener;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(complete = false, library = true)
public class ResponseSnifferModule {

   @Provides(type = Provides.Type.SET)
   ResponseListener provideNotificationCountListener(UserNotificationInteractor userNotificationInteractor) {
      return new RequestCountResponseListener(userNotificationInteractor);
   }

   @Singleton
   @Provides
   HttpResponseSnifferDelegate provideHttpResponseSnifferDelegate(Janet janet, Set<ResponseListener> listeners) {
      return new HttpResponseSnifferDelegate(janet, listeners);
   }
}
