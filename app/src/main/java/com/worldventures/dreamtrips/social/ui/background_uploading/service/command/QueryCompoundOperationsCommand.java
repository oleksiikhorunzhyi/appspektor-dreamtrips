package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class QueryCompoundOperationsCommand extends CompoundOperationsCommand {

   @Override
   protected void run(CommandCallback<List<PostCompoundOperationModel>> callback) throws Throwable {
      callback.onSuccess(cachedModels);
   }
}
