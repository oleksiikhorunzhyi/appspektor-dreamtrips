package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetPhotosFromGalleryCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SuggestedPhotoCommand extends Command<Boolean> implements InjectableAction {

   private static final int SUGGESTION_ITEM_CHUNK = 1;

   @Inject MediaInteractor mediaInteractor;
   @Inject SnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      mediaInteractor.getPhotosFromGalleryPipe()
            .createObservableResult(new GetPhotosFromGalleryCommand(SUGGESTION_ITEM_CHUNK))
            .map(Command::getResult)
            .map(items -> hasNewPhotos(items.get(0)))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private boolean hasNewPhotos(PhotoPickerModel photoPickerModel) {
      return photoPickerModel.getDateTaken() > snappyRepository.getLastSuggestedPhotosSyncTime();
   }
}
