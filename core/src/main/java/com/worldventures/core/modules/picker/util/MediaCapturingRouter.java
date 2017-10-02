package com.worldventures.core.modules.picker.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.worldventures.core.ui.view.routing.ActivityBoundRouter;
import com.worldventures.core.utils.FileUtils;

import java.io.File;


public class MediaCapturingRouter extends ActivityBoundRouter {

   public static final int CAPTURE_PICTURE_REQUEST_TYPE = 294;
   public static final int CAPTURE_VIDEO_REQUEST_TYPE = 295;

   public MediaCapturingRouter(Activity activity) {
      super(activity);
   }

   public String openCamera(String folderName) {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      String filePathOriginal = FileUtils.buildFilePathOriginal(folderName, "jpg");
      intent.putExtra("output", Uri.fromFile(new File(filePathOriginal)));
      startActivityResult(intent, CAPTURE_PICTURE_REQUEST_TYPE);
      return filePathOriginal;
   }

   public String openCameraForVideoRecording(String folderName, int durationLimitSecs) {
      Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      String filePathOriginal = FileUtils.buildFilePathOriginal(folderName, "mp4");
      intent.putExtra("output", Uri.fromFile(new File(filePathOriginal)));
      if (durationLimitSecs > 0) {
         intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, durationLimitSecs);
      }
      startActivityResult(intent, CAPTURE_VIDEO_REQUEST_TYPE);
      return filePathOriginal;
   }
}
