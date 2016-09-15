package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.api.janet.command.AcceptTermsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class LegalInteractor {

   private final ActionPipe<AcceptTermsCommand> termsPipe;

   @Inject
   public LegalInteractor(Janet janet) {
      termsPipe = janet.createPipe(AcceptTermsCommand.class, Schedulers.io());
   }

   public ActionPipe<AcceptTermsCommand> termsPipe() {
      return termsPipe;
   }
}
