package com.worldventures.dreamtrips.modules.common.service;


import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.command.GetVideoDurationCommand;
import com.worldventures.dreamtrips.modules.common.command.ImageCapturedCommand;
import com.worldventures.dreamtrips.modules.common.command.MediaCaptureCanceledCommand;
import com.worldventures.dreamtrips.modules.common.command.VideoCapturedCommand;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetMediaFromGalleryCommand;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetPhotosFromGalleryCommand;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetVideosFromGalleryCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class MediaInteractor {

   private final ActionPipe<ImageCapturedCommand> imageCapturedPipe;
   private final ActionPipe<VideoCapturedCommand> videoCapturedPipe;
   private final ActionPipe<CopyFileCommand> copyFilePipe;
   private final ActionPipe<GetPhotosFromGalleryCommand> getPhotosFromGalleryPipe;
   private final ActionPipe<GetVideosFromGalleryCommand> getVideosFromGalleryPipe;
   private final ActionPipe<GetMediaFromGalleryCommand> getMediaFromGalleryPipe;
   private final ActionPipe<GetVideoDurationCommand> getVideoDurationPipe;
   private final ActionPipe<MediaCaptureCanceledCommand> mediaCaptureCanceledPipe;

   @Inject
   public MediaInteractor(Janet janet) {
      this.imageCapturedPipe = janet.createPipe(ImageCapturedCommand.class, AndroidSchedulers.mainThread());
      this.videoCapturedPipe = janet.createPipe(VideoCapturedCommand.class, AndroidSchedulers.mainThread());
      this.copyFilePipe = janet.createPipe(CopyFileCommand.class, Schedulers.io());
      this.getPhotosFromGalleryPipe = janet.createPipe(GetPhotosFromGalleryCommand.class, Schedulers.io());
      this.getVideosFromGalleryPipe = janet.createPipe(GetVideosFromGalleryCommand.class, Schedulers.io());
      this.getMediaFromGalleryPipe = janet.createPipe(GetMediaFromGalleryCommand.class, Schedulers.io());
      this.getVideoDurationPipe = janet.createPipe(GetVideoDurationCommand.class, Schedulers.io());
      this.mediaCaptureCanceledPipe = janet.createPipe(MediaCaptureCanceledCommand.class, Schedulers.io());
   }

   public ActionPipe<ImageCapturedCommand> imageCapturedPipe() {
      return imageCapturedPipe;
   }

   public ActionPipe<VideoCapturedCommand> videoCapturedPipe() {
      return videoCapturedPipe;
   }

   public ActionPipe<CopyFileCommand> copyFilePipe() {
      return copyFilePipe;
   }

   public ActionPipe<GetPhotosFromGalleryCommand> getPhotosFromGalleryPipe() {
      return getPhotosFromGalleryPipe;
   }

   public ActionPipe<GetVideosFromGalleryCommand> getVideosFromGalleryPipe() {
      return getVideosFromGalleryPipe;
   }

   public ActionPipe<GetMediaFromGalleryCommand> getMediaFromGalleryPipe() {
      return getMediaFromGalleryPipe;
   }

   public ActionPipe<GetVideoDurationCommand> getVideoDurationPipe() {
      return getVideoDurationPipe;
   }

   public ActionPipe<MediaCaptureCanceledCommand> mediaCaptureCanceledPipe() {
      return mediaCaptureCanceledPipe;
   }
}
