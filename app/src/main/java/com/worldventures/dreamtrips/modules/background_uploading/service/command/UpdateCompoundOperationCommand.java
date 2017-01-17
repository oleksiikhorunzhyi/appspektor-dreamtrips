package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UpdateCompoundOperationCommand extends CompoundOperationsCommand {

   private CompoundOperationModel updatedModel;

   UpdateCompoundOperationCommand(CompoundOperationModel updatedModel) {
      this.updatedModel = updatedModel;
   }

   @Override
   protected void run(CommandCallback<List<CompoundOperationModel>> callback) throws Throwable {
      processUpdatedModel();
      callback.onSuccess(cachedModels);
   }

   private void processUpdatedModel() {
      int index = cachedModels.size();
      for (int i = 0; i < cachedModels.size(); i++) {
         if (cachedModels.get(i).id() == updatedModel.id()) {
            cachedModels.remove(i);
            index = i;
            break;
         }
      }
      cachedModels.add(index, updatedModel);
   }

}
