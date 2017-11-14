package com.worldventures.core.modules.picker.service.delegate;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.worldventures.core.modules.picker.model.VideoPickerModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class VideosProviderImpl implements VideosProvider {

   private final Context context;

   public VideosProviderImpl(Context context) {
      this.context = context;
   }

   @Override
   public List<VideoPickerModel> provide(int count) {
      Cursor cursor = null;
      try {
         String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_TAKEN,
               MediaStore.Video.Media.DURATION, MediaStore.Video.Media.MINI_THUMB_MAGIC};

         cursor = context.getContentResolver()
               .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                     MediaStore.Images.Media.DATE_TAKEN + " DESC");

         int dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
         int dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
         int durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
         List<VideoPickerModel> videos = new ArrayList<>();
         while (cursor.moveToNext() && count > 0) {
            count--;
            String path = cursor.getString(dataColumn);
            long dateTaken = cursor.getLong(dateColumn);
            long duration = cursor.getLong(durationColumn);

            VideoPickerModel video = new VideoPickerModel(path, duration, dateTaken);
            videos.add(video);
         }
         return videos;
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
