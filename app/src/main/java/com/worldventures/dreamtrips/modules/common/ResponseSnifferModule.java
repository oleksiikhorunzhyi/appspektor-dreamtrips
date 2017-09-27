package com.worldventures.dreamtrips.modules.common;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.janet.api_lib.ResponseListener;
import com.worldventures.dreamtrips.core.module.ApiModule;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.HttpResponseSnifferDelegate;
import com.worldventures.dreamtrips.social.ui.feed.api.response.HeaderChangedInformerListener;
import com.worldventures.dreamtrips.social.ui.feed.api.response.RequestCountResponseListener;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(complete = false, library = true)
public class ResponseSnifferModule {

   @Provides(type = Provides.Type.SET)
   ResponseListener provideNotificationCountListener(SnappyRepository db) {
      return new RequestCountResponseListener(db);
   }

   @Provides(type = Provides.Type.SET)
   ResponseListener provideHeaderChangedInformerResponseListener(NotificationCountEventDelegate notificationCountEventDelegate) {
      return new HeaderChangedInformerListener(notificationCountEventDelegate);
   }

   @Singleton
   @Provides
   HttpResponseSnifferDelegate provideHttpResponseSnifferDelegate(Janet janet, Set<ResponseListener> listeners) {
      return new HttpResponseSnifferDelegate(janet, listeners);
   }
}
