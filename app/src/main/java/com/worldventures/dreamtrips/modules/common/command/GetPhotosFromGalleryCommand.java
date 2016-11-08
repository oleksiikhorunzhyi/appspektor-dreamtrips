package com.worldventures.dreamtrips.modules.common.command;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class GetPhotosFromGalleryCommand extends Command<List<PhotoGalleryModel>> {

   private WeakReference<Context> contextWeakReference;

   public GetPhotosFromGalleryCommand(Context context) {
      this.contextWeakReference = new WeakReference<>(context);
   }

   @Override
   protected void run(CommandCallback<List<PhotoGalleryModel>> callback) throws Throwable {
      Cursor cursor = null;
      try {
         String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
         List<PhotoGalleryModel> photos = new ArrayList<>();
         cursor = MediaStore.Images.Media.query(contextWeakReference.get()
                     .getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionPhotos,
               MediaStore.Images.Media.MIME_TYPE + " != ?", new String[]{ImageUtils.MIME_TYPE_GIF},
               MediaStore.Images.Media.DATE_TAKEN + " DESC");
         int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
         int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
         while (cursor.moveToNext()) {
            String path = cursor.getString(dataColumn);
            long dateTaken = cursor.getLong(dateColumn);
            if (!ImageUtils.getImageExtensionFromPath(path).toLowerCase().contains("gif")) {
               PhotoGalleryModel photo = new PhotoGalleryModel(path, dateTaken);
               photos.add(photo);
            }
         }
         callback.onSuccess(photos);
      } catch (Throwable e) {
         callback.onFail(e);
         Timber.e(e.getMessage());
      } finally {
         if (cursor != null) {
            try {
               cursor.close();
            } catch (Exception e) {
               Timber.e(e.getMessage());
            }
         }
      }
   }

}
