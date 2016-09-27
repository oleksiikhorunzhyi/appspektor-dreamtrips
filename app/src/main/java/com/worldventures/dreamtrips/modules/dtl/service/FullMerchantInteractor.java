package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.schedulers.Schedulers;

public class FullMerchantInteractor {

   private final ActionPipe<MerchantByIdCommand> fullMerchantPipe;

   public FullMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      fullMerchantPipe = sessionActionPipeCreator.createPipe(MerchantByIdCommand.class, Schedulers.io());
   }

   public ReadActionPipe<MerchantByIdCommand> fullMerchantPipe() {
      return fullMerchantPipe.asReadOnly();
   }

   public void load(MerchantByIdCommand command) {
      fullMerchantPipe.send(command);
   }
}
