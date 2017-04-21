package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class QueryCompoundOperationsCommand extends CompoundOperationsCommand {

   @Override
   protected void run(CommandCallback<List<CompoundOperationModel>> callback) throws Throwable {
      callback.onSuccess(cachedModels);
   }
}
