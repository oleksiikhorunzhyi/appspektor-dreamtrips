package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UpdateCompoundOperationsCommand extends CompoundOperationsCommand {

   private List<PostCompoundOperationModel> updatedModels;

   UpdateCompoundOperationsCommand(List<PostCompoundOperationModel> updatedModels) {
      this.updatedModels = updatedModels;
   }

   @Override
   protected void run(CommandCallback<List<PostCompoundOperationModel>> callback) throws Throwable {
      callback.onSuccess(updatedModels);
   }
}
