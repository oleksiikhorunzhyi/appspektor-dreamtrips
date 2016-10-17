package com.worldventures.dreamtrips.modules.common.service;


import com.worldventures.dreamtrips.modules.common.command.ImageCapturedCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class MediaInteractor {

   private final ActionPipe<ImageCapturedCommand> imageCapturedPipe;

   @Inject
   public MediaInteractor(Janet janet) {
      this.imageCapturedPipe = janet.createPipe(ImageCapturedCommand.class, AndroidSchedulers.mainThread());
   }

   public ActionPipe<ImageCapturedCommand> imageCapturedPipe() {
      return imageCapturedPipe;
   }
}
