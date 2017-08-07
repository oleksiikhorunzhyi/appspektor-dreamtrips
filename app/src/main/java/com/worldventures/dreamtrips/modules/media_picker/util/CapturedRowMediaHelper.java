package com.worldventures.dreamtrips.modules.media_picker.util;

import android.util.Pair;

import com.worldventures.dreamtrips.modules.common.command.GetVideoDurationCommand;
import com.worldventures.dreamtrips.modules.common.command.VideoCapturedCommand;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils;

import rx.Observable;

public final class CapturedRowMediaHelper {

   private MediaInteractor mediaInteractor;
   private DrawableUtil drawableUtil;

   public CapturedRowMediaHelper(MediaInteractor mediaInteractor, DrawableUtil drawableUtil) {
      this.mediaInteractor = mediaInteractor;
      this.drawableUtil = drawableUtil;
   }

   public Observable<VideoPickerModel> videoModelFromCameraObservable() {
      return mediaInteractor.videoCapturedPipe()
            .observeSuccess()
            .map(VideoCapturedCommand::getUri)
            .flatMap(uri -> mediaInteractor.getVideoDurationPipe()
                  .createObservableResult(new GetVideoDurationCommand(uri)))
            .map(command -> new VideoPickerModel(command.getUri().getPath(), command.getResult()));
   }

   public Observable<PhotoPickerModel> photoModelFromCameraObservable() {
      return mediaInteractor.imageCapturedPipe()
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

}
