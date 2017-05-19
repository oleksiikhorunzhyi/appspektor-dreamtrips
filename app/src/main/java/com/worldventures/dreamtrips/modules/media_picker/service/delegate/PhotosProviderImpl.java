package com.worldventures.dreamtrips.modules.media_picker.service.delegate;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PhotosProviderImpl implements PhotosProvider {

   private Context context;

   public PhotosProviderImpl(Context context) {
      this.context = context;
   }

   @Override
   public List<PhotoPickerModel> provide() {
      Cursor cursor = null;
      try {
         String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
         List<PhotoPickerModel> photos = new ArrayList<>();
         cursor = MediaStore.Images.Media.query(context
                     .getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionPhotos,
               MediaStore.Images.Media.MIME_TYPE + " != ?", new String[]{ImageUtils.MIME_TYPE_GIF},
               MediaStore.Images.Media.DATE_TAKEN + " DESC");
         int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
         int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
         while (cursor.moveToNext()) {
            String path = cursor.getString(dataColumn);
            long dateTaken = cursor.getLong(dateColumn);
            photos.add(new PhotoPickerModel(path, dateTaken));
         }
         return photos;
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
