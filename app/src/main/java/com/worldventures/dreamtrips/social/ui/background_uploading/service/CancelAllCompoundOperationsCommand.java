package com.worldventures.dreamtrips.social.ui.background_uploading.service;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CancelAllCompoundOperationsCommand extends CompoundOperationsCommand implements InjectableAction {

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      synchronized (this) {
         backgroundUploadingInteractor.postProcessingPipe().cancelLatest();
         cachedModels.clear();
      }
      callback.onSuccess(cachedModels);
   }
}
