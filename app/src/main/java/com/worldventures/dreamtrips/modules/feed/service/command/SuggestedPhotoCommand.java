package com.worldventures.dreamtrips.modules.feed.service.command;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class SuggestedPhotoCommand extends Command<List<PhotoPickerModel>> implements InjectableAction {

   @Inject @ForApplication Context context;

   @Override
   protected void run(CommandCallback<List<PhotoPickerModel>> callback) throws Throwable {
      Observable.just(getGalleryPhotos()).subscribe(callback::onSuccess, callback::onFail);
   }

   private ArrayList<PhotoPickerModel> getGalleryPhotos() {
      Cursor cursor = null;
      String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
      ArrayList<PhotoPickerModel> photos = new ArrayList<>();
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
                  PhotoPickerModel photo = new PhotoPickerModel(path, dateTaken);
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
