package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteCompoundOperationsCommand extends CompoundOperationsCommand {

   private PostCompoundOperationModel removedModel;

   DeleteCompoundOperationsCommand(PostCompoundOperationModel removedModel) {
      this.removedModel = removedModel;
   }

   public PostCompoundOperationModel getRemovedModel() {
      return removedModel;
   }

   @Override
   protected void run(CommandCallback<List<PostCompoundOperationModel>> callback) throws Throwable {
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
