package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RestoreCompoundOperationsCommand extends Command<List<CompoundOperationModel>> implements InjectableAction {

   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   @Override
   protected void run(CommandCallback<List<CompoundOperationModel>> callback) throws Throwable {
      //When app started, it means that there can be commands which are in STARTED state, but actually they are not loading
      //We need to set that PAUSED state
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(new QueryCompoundOperationsCommand())
            .subscribe(command -> {
               List<CompoundOperationModel> cachedModels = command.getResult();
               for (int i = 0; i < cachedModels.size(); i++) {
                  CompoundOperationModel item = cachedModels.get(i);
                  if (item instanceof PostCompoundOperationModel &&
                        cachedModels.get(i).state() == CompoundOperationState.STARTED) {
                     cachedModels.remove(i);
                     cachedModels.add(i, ImmutablePostCompoundOperationModel.copyOf((PostCompoundOperationModel) item)
                           .withState(CompoundOperationState.PAUSED));
                  }
               }
               compoundOperationsInteractor.compoundOperationsPipe().send(CompoundOperationsCommand.compoundOperationsChanged(cachedModels));
               callback.onSuccess(cachedModels);
            }, callback::onFail);
   }
}
