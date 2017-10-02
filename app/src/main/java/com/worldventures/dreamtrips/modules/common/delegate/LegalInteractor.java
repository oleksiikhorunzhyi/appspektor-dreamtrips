package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.AcceptTermsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class LegalInteractor {

   private final ActionPipe<AcceptTermsCommand> termsPipe;

   @Inject
   public LegalInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      termsPipe = sessionActionPipeCreator.createPipe(AcceptTermsCommand.class, Schedulers.io());
   }

   public ActionPipe<AcceptTermsCommand> termsPipe() {
      return termsPipe;
   }
}
