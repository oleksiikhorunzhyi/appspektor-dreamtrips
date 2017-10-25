package com.worldventures.core.modules.auth.service;

import com.worldventures.dreamtrips.api.session.LoginHttpAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionService;
import io.techery.janet.Janet;

/**
 * Delegate for low-level re-authorization (skipping command, just plain *HttpAction)
 */
public class ReLoginInteractor {

   private final ActionPipe<LoginHttpAction> loginHttpActionPipe;

   public ReLoginInteractor(ActionService loginService) {
      loginHttpActionPipe = new Janet.Builder().addService(loginService).build().createPipe(LoginHttpAction.class);
   }

   public ActionPipe<LoginHttpAction> loginHttpActionPipe() {
      return loginHttpActionPipe;
   }
}
