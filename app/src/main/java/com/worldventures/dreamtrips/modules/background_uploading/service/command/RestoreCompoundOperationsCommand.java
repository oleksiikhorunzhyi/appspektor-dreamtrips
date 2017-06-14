package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RestoreCompoundOperationsCommand extends Command<List<PostCompoundOperationModel>> implements InjectableAction {

   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   @Override
   protected void run(CommandCallback<List<PostCompoundOperationModel>> callback) throws Throwable {
      //When app started, it means that there can be commands which are in STARTED state, but actually they are not loading
      //We need to set that PAUSED state
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(new QueryCompoundOperationsCommand())
            .subscribe(command -> {
               List<PostCompoundOperationModel> cachedModels = command.getResult();
               for (int i = 0; i < cachedModels.size(); i++) {
                  PostCompoundOperationModel item = cachedModels.get(i);
                  CompoundOperationState state = item.state();
                  switch (state) {
                     case STARTED:
                        cachedModels.remove(i);
                        cachedModels.add(i, ImmutablePostCompoundOperationModel.copyOf((PostCompoundOperationModel) item)
                              .withState(CompoundOperationState.PAUSED));
                        break;
                     case FINISHED:
                        cachedModels.remove(i);
                        break;
                  }
               }
               compoundOperationsInteractor.compoundOperationsPipe().send(CompoundOperationsCommand.compoundOperationsChanged(cachedModels));
               callback.onSuccess(cachedModels);
            }, callback::onFail);
   }
}
