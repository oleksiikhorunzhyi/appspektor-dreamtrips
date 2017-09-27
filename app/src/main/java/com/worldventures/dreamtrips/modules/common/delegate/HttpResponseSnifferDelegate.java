package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.core.janet.api_lib.ResponseListener;

import java.util.Set;

import io.techery.janet.Janet;

public class HttpResponseSnifferDelegate {

   private final Set<ResponseListener> responseListeners;

   public HttpResponseSnifferDelegate(Janet janet, Set<ResponseListener> responseListeners) {
      this.responseListeners = responseListeners;
      janet.createPipe(BaseHttpAction.class).observeSuccess().subscribe(this::touchListeners);
   }

   private void touchListeners(BaseHttpAction baseHttpAction) {
      if (responseListeners != null) {
         for (ResponseListener responseListener : responseListeners) {
            responseListener.onResponse(baseHttpAction);
         }
      }
   }
}
