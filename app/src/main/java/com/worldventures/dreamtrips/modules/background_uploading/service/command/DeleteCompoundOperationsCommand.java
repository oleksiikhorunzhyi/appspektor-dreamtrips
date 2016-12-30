package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteCompoundOperationsCommand extends CompoundOperationsCommand {

   private CompoundOperationModel removedModel;

   DeleteCompoundOperationsCommand(CompoundOperationModel removedModel) {
      this.removedModel = removedModel;
   }

   public CompoundOperationModel getRemovedModel() {
      return removedModel;
   }

   @Override
   protected void run(CommandCallback<List<CompoundOperationModel>> callback) throws Throwable {
      processUpdatedModel();
      callback.onSuccess(cachedModels);
   }

   private void processUpdatedModel() {
      for (int i = 0; i < cachedModels.size(); i++) {
         if (cachedModels.get(i).id() == removedModel.id()) {
            cachedModels.remove(i);
            break;
         }
      }
   }
}
