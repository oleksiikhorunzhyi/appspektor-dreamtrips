package com.worldventures.dreamtrips.modules.media_picker.service.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.service.delegate.PhotosProvider;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetPhotosFromGalleryCommand extends Command<List<PhotoPickerModel>> implements InjectableAction {

   @Inject PhotosProvider photosProvider;

   @Override
   protected void run(CommandCallback<List<PhotoPickerModel>> callback) throws Throwable {
      List<PhotoPickerModel> photos = photosProvider.provide();
      photos = Queryable.from(photos).filter((element, index) ->
            !ImageUtils.getImageExtensionFromPath(element.getAbsolutePath()).contains("gif")).toList();
      callback.onSuccess(photos);
   }
}
