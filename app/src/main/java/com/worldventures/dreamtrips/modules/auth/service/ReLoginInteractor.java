package com.worldventures.dreamtrips.modules.auth.service;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.api_common.converter.GsonProvider;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.HttpActionService;
import io.techery.janet.Janet;
import io.techery.janet.gson.GsonConverter;
import io.techery.janet.http.HttpClient;

/**
 * Delegate for low-level re-authorization (skipping command, just pure *HttpAction)
 */
public class ReLoginInteractor {

   private final ActionPipe<LoginHttpAction> loginHttpActionPipe;

   @Inject
   public ReLoginInteractor(HttpClient httpClient) {
      loginHttpActionPipe = new Janet.Builder()
            .addService(new HttpActionService(BuildConfig.DreamTripsApi, httpClient,
                  new GsonConverter(new GsonProvider().provideGson())))
            .build()
            .createPipe(LoginHttpAction.class);
   }

   public ActionPipe<LoginHttpAction> loginHttpActionPipe() {
      return loginHttpActionPipe;
   }
}
