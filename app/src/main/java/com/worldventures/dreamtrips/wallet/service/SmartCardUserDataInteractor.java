package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class SmartCardUserDataInteractor {

   private final ActionPipe<SmartCardAvatarCommand> smartCardAvatarPipe;
   private final ActionPipe<UpdateSmartCardUserCommand> updateSmartCardUserPipe;
   private final WriteActionPipe<RevertSmartCardUserUpdatingCommand> revertSmartCardUserUpdatingPipe;
   private final ActionPipe<RetryHttpUploadUpdatingCommand> retryHttpUploadUpdatingPipe;

   public SmartCardUserDataInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      smartCardAvatarPipe = sessionActionPipeCreator.createPipe(SmartCardAvatarCommand.class, Schedulers.io());
      updateSmartCardUserPipe = sessionActionPipeCreator.createPipe(UpdateSmartCardUserCommand.class, Schedulers.io());
      revertSmartCardUserUpdatingPipe = sessionActionPipeCreator.createPipe(RevertSmartCardUserUpdatingCommand.class, Schedulers.io());
      retryHttpUploadUpdatingPipe = sessionActionPipeCreator.createPipe(RetryHttpUploadUpdatingCommand.class, Schedulers.io());
   }

   public ActionPipe<SmartCardAvatarCommand> smartCardAvatarPipe() {
      return smartCardAvatarPipe;
   }

   public ActionPipe<UpdateSmartCardUserCommand> updateSmartCardUserPipe() {
      return updateSmartCardUserPipe;
   }

   public WriteActionPipe<RevertSmartCardUserUpdatingCommand> revertSmartCardUserUpdatingPipe() {
      return revertSmartCardUserUpdatingPipe;
   }

   public ActionPipe<RetryHttpUploadUpdatingCommand> retryHttpUploadUpdatingPipe() {
      return retryHttpUploadUpdatingPipe;
   }
}
