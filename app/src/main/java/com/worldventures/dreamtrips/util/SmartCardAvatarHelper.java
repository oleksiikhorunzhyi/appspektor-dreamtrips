package com.worldventures.dreamtrips.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.webkit.URLUtil;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;

import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.fromFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.saveToFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.scaleBitmap;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.floydSteinbergDither;

public class SmartCardAvatarHelper {

   private final Context context;

   @Inject
   SmartCardAvatarHelper(@ForApplication Context context) {
      this.context = context;
   }

   public File compressPhotoFromFile(String originImage) throws IOException {
      return compressPhotoFromFile(originImage, 0);
   }

   public File compressPhotoFromFile(String originImage, int imageSize) throws IOException {
      Bitmap bitmap = fromFile(originImage);
      if (imageSize > 0) {
         bitmap = scaleBitmap(bitmap, imageSize);
      }
      return saveToFile(context, bitmap);
   }

   public Observable<File> compressPhotoFromUrl(String url) {
      return compressPhotoFromUrl(url, 0);
   }

   public Observable<File> compressPhotoFromUrl(String url, int imageSize) {
      return ImageUtils.getBitmap(context, Uri.parse(url), imageSize, imageSize)
            .flatMap(bitmap -> Observable.fromCallable(() -> saveToFile(context, bitmap)));
   }

   public Observable<File> compressPhotoFromSchemePath(String schemePath) {
      return compressPhotoFromSchemePath(schemePath, 0);
   }

   public Observable<File> compressPhotoFromSchemePath(String schemePath, int imageSize) {
      if (URLUtil.isNetworkUrl(schemePath)) {
         return compressPhotoFromUrl(schemePath, imageSize);
      } else {
         return Observable.fromCallable(() -> compressPhotoFromFile(schemePath, imageSize));
      }
   }

   public int[][] toMonochrome(File imageFile) throws IOException {
      return toMonochrome(imageFile, 0);
   }

   public int[][] toMonochrome(File imageFile, int imageSize) throws IOException {
      Bitmap bitmap = fromFile(imageFile.getAbsolutePath());
      if (imageSize > 0) {
         bitmap = scaleBitmap(bitmap, imageSize);
      }
      return floydSteinbergDither(bitmap);
   }

   public byte[] convertBytesForUpload(int[][] pixelMatrix) {
      int height = pixelMatrix.length;
      int width = pixelMatrix[0].length;
      byte[] bytes = new byte[(width * height)];
      byte[] raw = new byte[(width * height) / 8];
      int pixel;
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            pixel = pixelMatrix[x][y];
            int byteIndex = y * width + x;
            if (pixel < 128) {
               bytes[byteIndex] = 0;
            } else {
               bytes[byteIndex] = 1;
            }
         }
      }
      int length = 0;
      for (int i = 0; i < bytes.length; i = i + 8) {
         byte first = bytes[i];
         for (int j = 0; j < 8; j++) {
            byte second = (byte) ((first << 1) | bytes[i + j]);
            first = second;
         }
         raw[length] = first;
         length++;
      }
      return raw;
   }


}
