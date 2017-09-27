package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class StartNextCompoundOperationCommand extends Command<Void> implements InjectableAction {

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(new QueryCompoundOperationsCommand())
            .map(Command::getResult)
            .subscribe(existingUploads -> {
               if (hasScheduledOperations(existingUploads) && !hasStartedOperations(existingUploads)) {
                  backgroundUploadingInteractor.postProcessingPipe()
                        .send(PostProcessingCommand.createPostProcessing(getNextOperation(existingUploads)));
               }
               callback.onSuccess(null);

            });
   }

   private boolean hasScheduledOperations(List<PostCompoundOperationModel> existingUploads) {
      return existingUploads != null && Queryable.from(existingUploads)
            .any(item -> item.state() == CompoundOperationState.SCHEDULED);
   }

   private boolean hasStartedOperations(List<PostCompoundOperationModel> existingUploads) {
      return existingUploads != null && Queryable.from(existingUploads)
            .any(item -> item.state() == CompoundOperationState.STARTED);
   }

   private PostCompoundOperationModel getNextOperation(List<PostCompoundOperationModel> existingUploads) {
      return Queryable.from(existingUploads)
            .first(item -> item.state() == CompoundOperationState.SCHEDULED);
   }
}
