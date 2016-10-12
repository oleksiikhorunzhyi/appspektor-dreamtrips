package com.worldventures.dreamtrips.core.api.uploadery;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class UploaderyInteractor {
   private final ActionPipe<UploaderyImageCommand> uploadImagePipe;

   @Inject
   public UploaderyInteractor(Janet janet) {
      this.uploadImagePipe = janet.createPipe(UploaderyImageCommand.class, Schedulers.io());
   }

   public ActionPipe<UploaderyImageCommand> uploadImageActionPipe() {
      return uploadImagePipe;
   }
}
