package com.worldventures.core.modules.picker.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.service.delegate.PhotosProvider;
import com.worldventures.core.utils.ImageUtils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetPhotosFromGalleryCommand extends Command<List<PhotoPickerModel>> implements InjectableAction {

   @Inject PhotosProvider photosProvider;

   private final int count;
   private final Date date;

   public GetPhotosFromGalleryCommand() {
      this(Integer.MAX_VALUE, new Date());
   }

   public GetPhotosFromGalleryCommand(int count) {
      this(count, new Date());
   }

   public GetPhotosFromGalleryCommand(int count, Date date) {
      this.count = count;
      this.date = date;
   }

   @Override
   protected void run(CommandCallback<List<PhotoPickerModel>> callback) throws Throwable {
      List<PhotoPickerModel> photos = photosProvider.provide(date, count);
      photos = Queryable.from(photos).filter((element, index) ->
            !ImageUtils.getImageExtensionFromPath(element.getAbsolutePath()).contains("gif")).toList();
      callback.onSuccess(photos);
   }
}
