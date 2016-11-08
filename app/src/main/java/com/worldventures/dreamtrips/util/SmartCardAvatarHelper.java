package com.worldventures.dreamtrips.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;

import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.fromFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.saveToFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.scaleBitmap;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.toMonochromeBitmap;

public class SmartCardAvatarHelper {

   private final Context context;

   @Inject
   SmartCardAvatarHelper(@ForApplication Context context) {
      this.context = context;
   }

   public File compressPhotoFromFile(String originImage, int imageSize) throws IOException {
      return toMonochromeFile(scaleBitmap(fromFile(originImage), imageSize));
   }

   public Observable<File> compressPhotoFromUrl(String url, int imageSize) {
      return ImageUtils.getBitmap(context, Uri.parse(url), imageSize, imageSize)
            .flatMap(bitmap -> Observable.fromCallable(() -> toMonochromeFile(bitmap)));
   }

   public File toMonochromeFile(Bitmap bitmap) throws IOException {
      return saveToFile(context, toMonochromeBitmap(bitmap));
   }

   public byte[] convertBytesForUpload(File file) {
      Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
      bitmap = scaleBitmap(bitmap, 256);
      int height = bitmap.getHeight();
      int width = bitmap.getWidth();
      byte[] bytes = new byte[(width * height)];
      byte[] raw = new byte[(width * height) / 8];
      int pixel;
      int k = 0;
      int b, g, r;
      for (int x = 0; x < height; x++) {
         for (int y = 0; y < width; y++, k++) {
            pixel = bitmap.getPixel(y, x);
            r = Color.red(pixel);
            g = Color.green(pixel);
            b = Color.blue(pixel);
            r = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            if (r < 128) {
               bytes[k] = 0;
            } else {
               bytes[k] = 1;
            }
         }
      }
      int length = 0;
      for (int i = 0; i < bytes.length; i = i + 8) {
         byte first = bytes[i];
         for (int j = 0; j < 7; j++) {
            byte second = (byte) ((first << 1) | bytes[i + j]);
            first = second;
         }
         raw[length] = first;
         length++;
      }
      return raw;
   }


}
