package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class ResumeCompoundOperationCommand extends Command<CompoundOperationModel> implements InjectableAction {

   private PostCompoundOperationModel compoundOperationModel;

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;

   public ResumeCompoundOperationCommand(PostCompoundOperationModel compoundOperationModel) {
      this.compoundOperationModel = compoundOperationModel;
   }

   @Override
   protected void run(CommandCallback<CompoundOperationModel> callback) throws Throwable {
      Observable.just(compoundOperationObjectMutator.resume(compoundOperationModel))
            .flatMap(this::notifyCompoundOperationChanged)
            .doOnNext(model -> startNext())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<CompoundOperationModel> notifyCompoundOperationChanged(CompoundOperationModel model) {
      return backgroundUploadingInteractor.compoundOperationsPipe()
            .createObservableResult(CompoundOperationsCommand.compoundCommandChanged(model))
            .map(command -> model);
   }

   private void startNext() {
      backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
   }
}
