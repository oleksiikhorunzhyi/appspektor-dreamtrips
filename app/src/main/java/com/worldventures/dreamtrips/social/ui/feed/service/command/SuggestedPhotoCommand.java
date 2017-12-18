package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.core.modules.picker.command.GetPhotosFromGalleryCommand;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository;
import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SuggestedPhotoCommand extends Command<Boolean> implements InjectableAction {

   private static final int SUGGESTION_ITEM_CHUNK = 1;

   @Inject MediaPickerInteractor mediaPickerInteractor;
   @Inject SocialSnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      mediaPickerInteractor.getPhotosFromGalleryPipe()
            .createObservableResult(new GetPhotosFromGalleryCommand(SUGGESTION_ITEM_CHUNK))
            .map(Command::getResult)
            .map(items -> hasNewPhotos(items.get(0)))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private boolean hasNewPhotos(PhotoPickerModel photoPickerModel) {
      return photoPickerModel.getDateTaken() > snappyRepository.getLastSuggestedPhotosSyncTime();
   }
}
