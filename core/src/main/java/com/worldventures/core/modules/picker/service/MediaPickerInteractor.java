package com.worldventures.core.modules.picker.service;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.picker.command.MediaAttachmentPrepareCommand;
import com.worldventures.core.modules.picker.command.GetMediaFromGalleryCommand;
import com.worldventures.core.modules.picker.command.GetPhotosFromGalleryCommand;
import com.worldventures.core.modules.picker.command.GetVideosFromGalleryCommand;
import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.command.GetVideoDurationCommand;
import com.worldventures.core.modules.picker.command.ImageCapturedCommand;
import com.worldventures.core.modules.picker.command.MediaCaptureCanceledCommand;
import com.worldventures.core.modules.picker.command.VideoCapturedCommand;

import io.techery.janet.ActionPipe;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MediaPickerInteractor {

   private final ActionPipe<ImageCapturedCommand> imageCapturedPipe;
   private final ActionPipe<VideoCapturedCommand> videoCapturedPipe;
   private final ActionPipe<CopyFileCommand> copyFilePipe;
   private final ActionPipe<GetPhotosFromGalleryCommand> getPhotosFromGalleryPipe;
   private final ActionPipe<GetVideosFromGalleryCommand> getVideosFromGalleryPipe;
   private final ActionPipe<GetMediaFromGalleryCommand> getMediaFromGalleryPipe;
   private final ActionPipe<GetVideoDurationCommand> getVideoDurationPipe;
   private final ActionPipe<MediaCaptureCanceledCommand> mediaCaptureCanceledPipe;
   private final ActionPipe<MediaAttachmentPrepareCommand> mediaAttachmentPreparePipe;

   public MediaPickerInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.imageCapturedPipe = sessionActionPipeCreator.createPipe(ImageCapturedCommand.class, AndroidSchedulers.mainThread());
      this.videoCapturedPipe = sessionActionPipeCreator.createPipe(VideoCapturedCommand.class, AndroidSchedulers.mainThread());
      this.copyFilePipe = sessionActionPipeCreator.createPipe(CopyFileCommand.class, Schedulers.io());
      this.getPhotosFromGalleryPipe = sessionActionPipeCreator.createPipe(GetPhotosFromGalleryCommand.class, Schedulers.io());
      this.getVideosFromGalleryPipe = sessionActionPipeCreator.createPipe(GetVideosFromGalleryCommand.class, Schedulers.io());
      this.getMediaFromGalleryPipe = sessionActionPipeCreator.createPipe(GetMediaFromGalleryCommand.class, Schedulers.io());
      this.getVideoDurationPipe = sessionActionPipeCreator.createPipe(GetVideoDurationCommand.class, Schedulers.io());
      this.mediaCaptureCanceledPipe = sessionActionPipeCreator.createPipe(MediaCaptureCanceledCommand.class, Schedulers.io());
      this.mediaAttachmentPreparePipe = sessionActionPipeCreator.createPipe(MediaAttachmentPrepareCommand.class, Schedulers.computation());
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

   public ActionPipe<MediaAttachmentPrepareCommand> mediaAttachmentPreparePipe() {
      return mediaAttachmentPreparePipe;
   }
}
