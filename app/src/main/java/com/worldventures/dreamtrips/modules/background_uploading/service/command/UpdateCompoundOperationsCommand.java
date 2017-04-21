package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UpdateCompoundOperationsCommand extends CompoundOperationsCommand {

   private List<CompoundOperationModel> updatedModels;

   UpdateCompoundOperationsCommand(List<CompoundOperationModel> updatedModels) {
      this.updatedModels = updatedModels;
   }

   @Override
   protected void run(CommandCallback<List<CompoundOperationModel>> callback) throws Throwable {
      callback.onSuccess(updatedModels);
   }
}
