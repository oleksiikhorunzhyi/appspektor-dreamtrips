package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DrawableUtil {

   public static final int THUMBNAIL_BIG = 1;
   private static final int FULL_HD_WIDTH = 1920;

   public static final String CACHE_DIR = "dreamtrips_cache_images";

   private Context context;

   public DrawableUtil(Context context) {
      this.context = context;
   }

   public Drawable copyIntoDrawable(Bitmap bitmap) {
      return new BitmapDrawable(context.getResources(), copyBitmap(bitmap));
   }

   public Bitmap copyBitmap(Bitmap bitmap) {
      return bitmap.copy(bitmap.getConfig(), true);
   }

   public Pair<String, Size> compressAndRotateImage(String fileImage, int scale) {
      FileOutputStream stream = null;
      Bitmap bitmap = null;
      try {
         BitmapFactory.Options optionsForGettingDimensions = new BitmapFactory.Options();
         optionsForGettingDimensions.inJustDecodeBounds = true;
         bitmap = BitmapFactory.decodeFile(fileImage, optionsForGettingDimensions);
         if (bitmap != null) {
            bitmap.recycle();
         }
         int w, l;
         w = optionsForGettingDimensions.outWidth;
         l = optionsForGettingDimensions.outHeight;

         int rotate = obtainRotation(fileImage);

         BitmapFactory.Options options = new BitmapFactory.Options();
         options.inSampleSize = getInSampleSize(w, l, scale);
         options.inJustDecodeBounds = false;

         bitmap = BitmapFactory.decodeFile(fileImage, options);

         File originalFile = new File(fileImage);
         File newFile = new File(getImagesCacheDir(), originalFile.getName());
         stream = new FileOutputStream(newFile);
         if (rotate != 0) {
            Matrix matrix = new Matrix();
            matrix.setRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
         }

         bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

         List<Pair<String, String>> customParams = new ArrayList<>();
         customParams.add(new Pair<>(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL)));

         try {
            ExifUtils.copyExif(originalFile.getAbsolutePath(), newFile.getAbsolutePath(), customParams);
         } catch (IOException e) {
            Timber.e(e, "Failed to copy exif");
         }

         return new Pair<>(newFile.getAbsolutePath(), new Size(bitmap.getWidth(), bitmap.getHeight()));
      } catch (Exception e) {
         return new Pair<>(fileImage, new Size(0, 0));
      } finally {
         try {
            if (bitmap != null) bitmap.recycle();
            stream.flush();
            stream.close();
         } catch (Exception e) {
            Timber.e(e.getMessage());
         }
      }
   }

   private int getInSampleSize(int width, int height, int scale) {
      int what = width > height ? width : height;
      int sampleSize;
      if (what > FULL_HD_WIDTH) {
         sampleSize = scale * (int) Math.round((double) what / FULL_HD_WIDTH);
      } else {
         sampleSize = scale;
      }

      return sampleSize;
   }

   public Size getImageSizeFromUrl(String baseUrl, int scale) {
      try {
         URL url = new URL(baseUrl);
         Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
         int width = bitmap.getWidth();
         int height = bitmap.getHeight();
         int inSampleSize = getInSampleSize(width, height, scale);
         return new Size(width / inSampleSize, height / inSampleSize);
      } catch (IOException e) {
         Timber.e(e.getMessage());
         return null;
      }
   }

   public int obtainRotation(String fileImage) throws IOException {
      ExifInterface exif = new ExifInterface(fileImage);

      int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      int rotate = 0;
      switch (orientation) {
         case ExifInterface.ORIENTATION_ROTATE_270:
            rotate = -90;
            break;
         case ExifInterface.ORIENTATION_ROTATE_180:
            rotate = 180;
            break;
         case ExifInterface.ORIENTATION_ROTATE_90:
            rotate = 90;
            break;
      }

      return rotate;
   }

   public void removeCacheImages(List<String> filteredPathes) {
      File[] cachedFiles = getImagesCacheDir().listFiles();
      for (File cachedFile : cachedFiles) {
         if (!filteredPathes.contains(cachedFile.getAbsolutePath())) {
            cachedFile.delete();
         }
      }
   }

   public File getImagesCacheDir() {
      File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
      cacheDir.mkdir();
      return cacheDir;
   }
}
