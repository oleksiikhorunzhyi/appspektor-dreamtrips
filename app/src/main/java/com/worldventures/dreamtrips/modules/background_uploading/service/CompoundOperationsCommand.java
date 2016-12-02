package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CompoundOperationsCommand extends Command<List<CompoundOperationModel>> {

   private List<CompoundOperationModel> dummyModels = new ArrayList<>();

   @Override
   protected void run(CommandCallback<List<CompoundOperationModel>> callback) throws Throwable {
      callback.onSuccess(dummyModels);
   }
}
