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
public class ScheduleCompoundOperationCommand extends Command<Void> implements InjectableAction {

   private PostCompoundOperationModel compoundOperationModel;

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   public ScheduleCompoundOperationCommand(PostCompoundOperationModel compoundOperationModel) {
      this.compoundOperationModel = compoundOperationModel;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(CompoundOperationsCommand.compoundCommandChanged(compoundOperationModel))
            .subscribe(command -> {
               if (!hasStartedUploads(command.getResult())) {
                  backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
               }
               callback.onSuccess(null);
            });
   }

   private boolean hasStartedUploads(List<CompoundOperationModel> existingUploads) {
      return Queryable.from(existingUploads)
            .firstOrDefault(element -> element.state() == CompoundOperationState.STARTED) != null;
   }
}
