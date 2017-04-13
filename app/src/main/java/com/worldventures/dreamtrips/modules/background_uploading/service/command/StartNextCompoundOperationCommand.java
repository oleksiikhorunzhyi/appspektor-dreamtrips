package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;

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
                  backgroundUploadingInteractor.postProcessingPipe().send(new PostProcessingCommand(getNextOperation(existingUploads)));
               }
               callback.onSuccess(null);

            });
   }

   private boolean hasScheduledOperations(List<CompoundOperationModel> existingUploads) {
      return existingUploads != null && Queryable.from(existingUploads)
            .any(item -> item.state() == CompoundOperationState.SCHEDULED);
   }

   private boolean hasStartedOperations(List<CompoundOperationModel> existingUploads) {
      return existingUploads != null && Queryable.from(existingUploads)
            .any(item -> item.state() == CompoundOperationState.STARTED);
   }

   private PostCompoundOperationModel getNextOperation(List<CompoundOperationModel> existingUploads) {
      return Queryable.from(existingUploads)
            .cast(PostCompoundOperationModel.class)
            .first(item -> item.state() == CompoundOperationState.SCHEDULED);
   }
}
