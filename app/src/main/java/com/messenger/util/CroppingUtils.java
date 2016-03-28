package com.messenger.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.yalantis.ucrop.UCrop;

import java.io.File;


public class CroppingUtils {

    public static void startCropping(Activity activity, String fileFrom, String fileTo, int ratioX, int ratioY){
        obtainBasicUCrop(fileFrom, fileTo)
                .withAspectRatio(ratioX, ratioY)
                .start(activity);
    }

    public static void startCropping(Context context, Fragment fragment, String fileFrom, String fileTo, int ratioX, int ratioY){
        obtainBasicUCrop(fileFrom, fileTo)
                .withAspectRatio(ratioX, ratioY)
                .start(context, fragment);
    }

    public static void startCropping(Activity activity, String fileFrom, String fileTo){
        obtainBasicUCrop(fileFrom, fileTo).start(activity);
    }

    private static UCrop obtainBasicUCrop(String fileFrom, String fileTo){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        Uri from = Uri.fromFile(new File(fileFrom));
        Uri to = Uri.fromFile(new File(fileTo));
        return UCrop.of(from, to).withOptions(options);
    }

    public static boolean isCroppingResult(int requestCode, int resultCode){
        return requestCode == UCrop.REQUEST_CROP || resultCode == UCrop.RESULT_ERROR;
    }

    public static Pair<String, Throwable> obtainResults(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            return new Pair<>(resultUri.getPath(), null);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            return new Pair<>(null, UCrop.getError(data));
        }
        return null;
    }

}
