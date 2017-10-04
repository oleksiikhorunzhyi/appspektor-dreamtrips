package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class ResumeCompoundOperationCommand extends Command<PostCompoundOperationModel> implements InjectableAction {

   private PostCompoundOperationModel compoundOperationModel;

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;

   public ResumeCompoundOperationCommand(PostCompoundOperationModel compoundOperationModel) {
      this.compoundOperationModel = compoundOperationModel;
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel> callback) throws Throwable {
      Observable.just(compoundOperationObjectMutator.resume(compoundOperationModel))
            .flatMap(this::notifyCompoundOperationChanged)
            .doOnNext(model -> startNext())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<PostCompoundOperationModel> notifyCompoundOperationChanged(PostCompoundOperationModel model) {
      return compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(CompoundOperationsCommand.compoundCommandChanged(model))
            .map(command -> model);
   }

   private void startNext() {
      backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
   }
}
