package com.worldventures.dreamtrips.wallet.service.nxt;

import com.techery.spares.session.NxtSessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse;
import com.worldventures.dreamtrips.wallet.service.nxt.model.NxtSession;

import java.util.Arrays;
import java.util.List;

import rx.functions.Func0;

public class NxtAuthRetryPolicy {

   private final NxtSessionHolder nxtSessionHolder;
   private final List<Integer> sessionErrorCodes = Arrays.asList(11001, 11002, 10003, 10004);

   public NxtAuthRetryPolicy(NxtSessionHolder nxtSessionHolder) {
      this.nxtSessionHolder = nxtSessionHolder;
   }

   public boolean handle(MultiErrorResponse apiError, Func0<NxtSession> createNxtSessionCall) {
      if (shouldRetry(apiError)) {
         NxtSession nxtSession = createNxtSessionCall.call();
         if (nxtSession != null) {
            handleSession(nxtSession);
            return true;
         }
      }
      return false;
   }

   private void handleSession(NxtSession nxtSession) {
      nxtSessionHolder.put(nxtSession);
   }

   private boolean shouldRetry(MultiErrorResponse apiError) {
      return isLoginError(apiError) && isCredentialExists();
   }

   private boolean isLoginError(MultiErrorResponse apiError) {
      return apiError != null && sessionErrorCodes.contains(apiError.getCode());
   }

   private boolean isCredentialExists() {
      Optional<NxtSession> nxtSessionOptional = nxtSessionHolder.get();
      if (nxtSessionOptional.isPresent()) {
         NxtSession nxtSession = nxtSessionHolder.get().get();
         return nxtSession.token() != null && !nxtSession.token().isEmpty();
      } else {
         return false;
      }
   }
}
