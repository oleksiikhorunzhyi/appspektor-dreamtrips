package com.worldventures.core.modules.picker.util;

import android.util.Pair;

import com.worldventures.core.modules.picker.command.GetVideoDurationCommand;
import com.worldventures.core.modules.picker.command.VideoCapturedCommand;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.ui.util.DrawableUtil;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.core.utils.Size;

import java.io.IOException;

import rx.Observable;

public final class CapturedRowMediaHelper {

   private final MediaPickerInteractor mediaPickerInteractor;
   private final DrawableUtil drawableUtil;

   public CapturedRowMediaHelper(MediaPickerInteractor mediaPickerInteractor, DrawableUtil drawableUtil) {
      this.mediaPickerInteractor = mediaPickerInteractor;
      this.drawableUtil = drawableUtil;
   }

   public Observable<VideoPickerModel> videoModelFromCameraObservable() {
      return mediaPickerInteractor.videoCapturedPipe()
            .observeSuccess()
            .map(VideoCapturedCommand::getUri)
            .flatMap(uri -> mediaPickerInteractor.getVideoDurationPipe()
                  .createObservableResult(new GetVideoDurationCommand(uri)))
            .map(command -> new VideoPickerModel(command.getUri().getPath(), command.getResult()));
   }

   public Observable<PhotoPickerModel> photoModelFromCameraObservable() {
      return mediaPickerInteractor.imageCapturedPipe()
            .observeSuccess()
            .map(imageCapturedCommand -> processPhotoModel(imageCapturedCommand.getResult()));
   }

   public PhotoPickerModel processPhotoModel(String pickedPath) {
      Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, pickedPath);
      return new PhotoPickerModel(pair.first, pair.second);
   }

   public Pair<String, Size> generateUri(String path) {
      return ImageUtils.generateUri(drawableUtil, path);
   }

   public int obtainPhotoOrientation(String path) {
      try {
         return drawableUtil.obtainRotation(path);
      } catch (IOException e) {
         return 0;
      }
   }

}
