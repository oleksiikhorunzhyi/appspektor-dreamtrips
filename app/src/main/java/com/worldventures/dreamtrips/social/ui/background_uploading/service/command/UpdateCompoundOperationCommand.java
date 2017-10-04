package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UpdateCompoundOperationCommand extends CompoundOperationsCommand {

   private PostCompoundOperationModel updatedModel;

   UpdateCompoundOperationCommand(PostCompoundOperationModel updatedModel) {
      this.updatedModel = updatedModel;
   }

   @Override
   protected void run(CommandCallback<List<PostCompoundOperationModel>> callback) throws Throwable {
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
