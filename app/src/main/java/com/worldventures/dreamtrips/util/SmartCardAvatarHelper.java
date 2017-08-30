package com.worldventures.dreamtrips.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;

import com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;

import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.getBitmap;
import static rx.Observable.fromCallable;

public class SmartCardAvatarHelper {

   public final Context context;

   @Inject
   SmartCardAvatarHelper(Context context) {
      this.context = context;
   }

   public Observable<byte[]> toSmartCardPhoto(String imageUri) {
      return getBitmap(context, Uri.parse(imageUri), ImageUtils.DEFAULT_IMAGE_SIZE, ImageUtils.DEFAULT_IMAGE_SIZE)
            .flatMap(bitmap -> fromCallable(() -> toMonochrome(bitmap, ImageUtils.DEFAULT_IMAGE_SIZE)))
            .map(this::convertBytesForUpload);
   }

   private int[][] toMonochrome(Bitmap bitmap, int imageSize) throws IOException {
      if (imageSize > 0) {
         bitmap = ThumbnailUtils.extractThumbnail(bitmap, imageSize, imageSize);
      }
      return floydSteinbergDither(bitmap);
   }

   private static int[][] floydSteinbergDither(Bitmap src) {
      int[][] pixelMatrix = convertBimapToArray(src);
      int error;
      int width = src.getWidth();
      int height = src.getHeight();
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            error = evaluateError(pixelMatrix, x, y);
            if (x + 1 < width) {
               replaceSurroundingPixel(pixelMatrix, x + 1, y, error, 7);
            }
            if (x - 1 >= 0 && y + 1 < height) {
               replaceSurroundingPixel(pixelMatrix, x - 1, y + 1, error, 3);
            }
            if (y + 1 < height) {
               replaceSurroundingPixel(pixelMatrix, x, y + 1, error, 5);
            }
            if (x + 1 < width && y + 1 < height) {
               replaceSurroundingPixel(pixelMatrix, x + 1, y + 1, error, 1);
            }
         }
      }
      return pixelMatrix;
   }

   private static int[][] convertBimapToArray(Bitmap src) {
      int[][] matrix = new int[src.getWidth()][src.getHeight()];
      for (int y = 0; y < src.getHeight(); y++) {
         for (int x = 0; x < src.getWidth(); x++) {
            matrix[x][y] = Color.red(src.getPixel(x, y));
         }
      }
      return matrix;
   }

   private static int evaluateError(int[][] pixelMatrix, int x, int y) {
      int oldPixel = pixelMatrix[x][y];
      int newPixel = oldPixel > 128 ? 255 : 0;
      pixelMatrix[x][y] = newPixel;
      return oldPixel - newPixel;
   }

   private static void replaceSurroundingPixel(int[][] pixelMatrix, int x, int y, int error, int multiplier) {
      int oldPixel = pixelMatrix[x][y];
      int newPixel = oldPixel + (error * multiplier) / 16;
      int dithered = Math.max(Math.min(newPixel, 250), 0);
      pixelMatrix[x][y] = dithered;
   }

   private byte[] convertBytesForUpload(int[][] pixelMatrix) {
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
