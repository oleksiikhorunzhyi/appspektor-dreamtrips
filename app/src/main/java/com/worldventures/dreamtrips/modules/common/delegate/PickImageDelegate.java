package com.worldventures.dreamtrips.modules.common.delegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.command.ImageCapturedCommand;
import com.worldventures.dreamtrips.modules.common.command.MediaCaptureCanceledCommand;
import com.worldventures.dreamtrips.modules.common.command.VideoCapturedCommand;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;

import icepick.Icepick;
import icepick.State;

public class PickImageDelegate {

   public static final String FOLDERNAME = "DreamTrips/temp";

   private ActivityRouter activityRouter;
   private MediaInteractor mediaInteractor;

   @State String filePath;

   public PickImageDelegate(ActivityRouter activityRouter, MediaInteractor mediaInteractor) {
      this.activityRouter = activityRouter;
      this.mediaInteractor = mediaInteractor;
   }

   public void saveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   public void restoreInstanceState(Bundle savedState) {
      Icepick.restoreInstanceState(this, savedState);
   }

   public void takePicture() {
      filePath = activityRouter.openCamera(FOLDERNAME);
   }

   public void recordVideo(int durationLimitSecs) {
      filePath = activityRouter.openCameraForVideoRecording(FOLDERNAME, durationLimitSecs);
   }

   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
         case ActivityRouter.CAPTURE_PICTURE_REQUEST_TYPE:
            if (resultCode == Activity.RESULT_OK) {
               mediaInteractor.imageCapturedPipe().send(new ImageCapturedCommand(filePath));
            } else if (resultCode == Activity.RESULT_CANCELED) {
               mediaInteractor.mediaCaptureCanceledPipe()
                     .send(new MediaCaptureCanceledCommand(MediaPickerModel.Type.PHOTO));
            }
            break;
         case ActivityRouter.CAPTURE_VIDEO_REQUEST_TYPE:
            if (resultCode == Activity.RESULT_OK) {
               mediaInteractor.videoCapturedPipe().send(new VideoCapturedCommand(filePath));
            } else if (resultCode == Activity.RESULT_CANCELED) {
               mediaInteractor.mediaCaptureCanceledPipe()
                     .send(new MediaCaptureCanceledCommand(MediaPickerModel.Type.VIDEO));
            }
            break;
      }
   }
}
