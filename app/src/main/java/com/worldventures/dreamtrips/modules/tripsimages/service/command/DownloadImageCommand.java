package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class DownloadImageCommand extends Command<String> implements InjectableAction {

   @Inject @ForApplication Context context;

   private String url;

   public DownloadImageCommand(String url) {
      this.url = url;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      callback.onSuccess(cacheBitmap());
   }

   private String cacheBitmap() throws Exception {
      Bitmap bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
      return insertImage(bitmap, CachedEntity.getFileName(url));
   }

   /**
    * Use for save image to gallery instead of {@link android.provider.MediaStore.Images.Media},
    * because we need to change {@link MediaStore.Images.Media#DATE_TAKEN}
    *
    * @param source Bitmap decoded from stream
    * @param title  The name of image
    * @return Uri for newly created image
    */
   private String insertImage(Bitmap source, String title) {
      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.TITLE, title);
      values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
      //
      Uri url = null;
      String stringUrl = null;
      ContentResolver cr = context.getContentResolver();
      //
      try {
         url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

         if (source != null) {
            OutputStream imageOut = cr.openOutputStream(url);
            try {
               source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
            } finally {
               imageOut.close();
            }

            long id = ContentUris.parseId(url);
            Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
         } else {
            Timber.e("Failed to create thumbnail, removing original");
            cr.delete(url, null, null);
            url = null;
         }
      } catch (Exception e) {
         Timber.e(e, "Failed to insert image");
         if (url != null) {
            cr.delete(url, null, null);
            url = null;
         }
      }
      //
      if (url != null) stringUrl = url.toString();

      return stringUrl;
   }

   private Bitmap storeThumbnail(ContentResolver cr, Bitmap source, long id, float width, float height, int kind) {
      Matrix matrix = new Matrix();
      //
      float scaleX = width / source.getWidth();
      float scaleY = height / source.getHeight();
      matrix.setScale(scaleX, scaleY);
      //
      Bitmap thumb = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
      //
      ContentValues values = new ContentValues(4);
      values.put(MediaStore.Images.Thumbnails.KIND, kind);
      values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
      values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
      values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());
      //
      Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);
      //
      try {
         OutputStream thumbOut = cr.openOutputStream(url);

         thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
         thumbOut.close();
         return thumb;
      } catch (FileNotFoundException ex) {
         return null;
      } catch (IOException ex) {
         return null;
      }
   }
}
