package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CancelCompoundOperationCommand extends Command implements InjectableAction {

   private CompoundOperationModel compoundOperationModel;

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   public CancelCompoundOperationCommand(CompoundOperationModel compoundOperationModel) {
      this.compoundOperationModel = compoundOperationModel;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      if (compoundOperationModel.state() == CompoundOperationState.STARTED) {
         backgroundUploadingInteractor.postProcessingPipe().cancelLatest();
      }
      backgroundUploadingInteractor.compoundOperationsPipe()
            .createObservable(CompoundOperationsCommand.compoundCommandRemoved(compoundOperationModel))
            .doOnNext(command -> startNext())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void startNext() {
      backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
   }
}
