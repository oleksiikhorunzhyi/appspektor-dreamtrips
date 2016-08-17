package com.worldventures.dreamtrips.modules.feed.api;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class PhotoGalleryRequest extends SpiceRequest<ArrayList<PhotoGalleryModel>> {

   private Context context;

   public PhotoGalleryRequest(Context context) {
      super((Class<ArrayList<PhotoGalleryModel>>) new ArrayList<PhotoGalleryModel>().getClass());
      this.context = context;
   }

   @Override
   public ArrayList<PhotoGalleryModel> loadDataFromNetwork() throws Exception {
      return getGalleryPhotos();
   }

   private ArrayList<PhotoGalleryModel> getGalleryPhotos() {
      Cursor cursor = null;
      String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
      ArrayList<PhotoGalleryModel> photos = new ArrayList<>();
      //
      try {
         cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionPhotos, MediaStore.Images.Media.MIME_TYPE + " != ?", new String[]{ImageUtils.MIME_TYPE_GIF}, MediaStore.Images.Media.DATE_TAKEN + " DESC");
         if (cursor != null) {
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
         }
      } catch (Throwable e) {
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
      return photos;
   }
}
