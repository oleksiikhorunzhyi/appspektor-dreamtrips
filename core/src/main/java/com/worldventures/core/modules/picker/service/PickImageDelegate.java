package com.worldventures.core.modules.picker.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.worldventures.core.modules.picker.model.MediaPickerModel;
import com.worldventures.core.modules.picker.util.MediaCapturingRouter;
import com.worldventures.core.modules.picker.command.ImageCapturedCommand;
import com.worldventures.core.modules.picker.command.MediaCaptureCanceledCommand;
import com.worldventures.core.modules.picker.command.VideoCapturedCommand;

import icepick.Icepick;
import icepick.State;

public class PickImageDelegate {

   public static final String FOLDERNAME = "DreamTrips/temp";

   private MediaCapturingRouter mediaCapturingRouter;
   private MediaPickerInteractor mediaPickerInteractor;

   @State String filePath;

   public PickImageDelegate(MediaCapturingRouter mediaCapturingRouter, MediaPickerInteractor mediaPickerInteractor) {
      this.mediaCapturingRouter = mediaCapturingRouter;
      this.mediaPickerInteractor = mediaPickerInteractor;
   }

   public void saveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   public void restoreInstanceState(Bundle savedState) {
      Icepick.restoreInstanceState(this, savedState);
   }

   public void takePicture() {
      filePath = mediaCapturingRouter.openCamera(FOLDERNAME);
   }

   public void recordVideo(int durationLimitSecs) {
      filePath = mediaCapturingRouter.openCameraForVideoRecording(FOLDERNAME, durationLimitSecs);
   }

   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
         case MediaCapturingRouter.CAPTURE_PICTURE_REQUEST_TYPE:
            if (resultCode == Activity.RESULT_OK) {
               mediaPickerInteractor.imageCapturedPipe().send(new ImageCapturedCommand(filePath));
            } else if (resultCode == Activity.RESULT_CANCELED) {
               mediaPickerInteractor.mediaCaptureCanceledPipe()
                     .send(new MediaCaptureCanceledCommand(MediaPickerModel.Type.PHOTO));
            }
            break;
         case MediaCapturingRouter.CAPTURE_VIDEO_REQUEST_TYPE:
            if (resultCode == Activity.RESULT_OK) {
               mediaPickerInteractor.videoCapturedPipe().send(new VideoCapturedCommand(filePath));
            } else if (resultCode == Activity.RESULT_CANCELED) {
               mediaPickerInteractor.mediaCaptureCanceledPipe()
                     .send(new MediaCaptureCanceledCommand(MediaPickerModel.Type.VIDEO));
            }
            break;
      }
   }
}
