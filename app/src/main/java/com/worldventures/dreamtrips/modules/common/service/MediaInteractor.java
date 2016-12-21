package com.worldventures.dreamtrips.modules.common.service;


import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.command.GetPhotosFromGalleryCommand;
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
   private final ActionPipe<CopyFileCommand> copyFilePipe;
   private final ActionPipe<GetPhotosFromGalleryCommand> getPhotosFromGalleryPipe;

   @Inject
   public MediaInteractor(Janet janet) {
      this.imageCapturedPipe = janet.createPipe(ImageCapturedCommand.class, AndroidSchedulers.mainThread());
      this.copyFilePipe = janet.createPipe(CopyFileCommand.class, Schedulers.io());
      this.getPhotosFromGalleryPipe = janet.createPipe(GetPhotosFromGalleryCommand.class, Schedulers.io());
   }

   public ActionPipe<ImageCapturedCommand> imageCapturedPipe() {
      return imageCapturedPipe;
   }

   public ActionPipe<CopyFileCommand> copyFilePipe() {
      return copyFilePipe;
   }

   public ActionPipe<GetPhotosFromGalleryCommand> getPhotosFromGalleryPipe() {
      return getPhotosFromGalleryPipe;
   }
}
