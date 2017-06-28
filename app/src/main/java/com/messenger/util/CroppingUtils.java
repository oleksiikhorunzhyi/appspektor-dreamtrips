package com.messenger.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.worldventures.dreamtrips.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;


public class CroppingUtils {

   public static void startCropping(Activity activity, Uri fileFrom, Uri fileTo, int ratioX, int ratioY) {
      obtainBasicUCrop(activity, fileFrom, fileTo).withAspectRatio(ratioX, ratioY).start(activity);
   }

   public static void startCropping(Context context, Fragment fragment, int requestCode, String fileFrom, String fileTo, int ratioX, int ratioY) {
      obtainBasicUCrop(context, Uri.fromFile(new File(fileFrom)), Uri.fromFile(new File(fileTo))).withAspectRatio(ratioX, ratioY).start(context, fragment, requestCode);
   }

   private static UCrop obtainBasicUCrop(Context context, Uri from, Uri to) {
      UCrop.Options options = new UCrop.Options();
      options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

      options.setToolbarColor(obtainColor(R.color.theme_main, context));
      options.setActiveWidgetColor(obtainColor(R.color.cropping_selected_button, context));
      options.setStatusBarColor(obtainColor(R.color.accent, context));

      return UCrop.of(from, to).withOptions(options);
   }

   private static int obtainColor(int id, Context context) {
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
         return context.getColor(id);
      } else {
         return context.getResources().getColor(id);
      }
   }

   public static boolean isCroppingResult(int requestCode, int resultCode) {
      return requestCode == UCrop.REQUEST_CROP || resultCode == UCrop.RESULT_ERROR;
   }

   public static Pair<String, Throwable> obtainResults(int requestCode, int resultCode, Intent data) {
      if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
         final Uri resultUri = UCrop.getOutput(data);
         return new Pair<>(resultUri.getPath(), null);
      } else if (resultCode == UCrop.RESULT_ERROR) {
         return new Pair<>(null, UCrop.getError(data));
      }
      return null;
   }

}
