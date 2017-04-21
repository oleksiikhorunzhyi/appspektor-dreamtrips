package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class EditPhotoWithTagsCommand extends CommandWithError<Photo> implements InjectableAction {

   private String uid;
   private UploadTask task;
   private List<PhotoTag> addedTags;
   private List<PhotoTag> removedTags;

   @Inject TripImagesInteractor tripImagesInteractor;

   public EditPhotoWithTagsCommand(String uid, UploadTask task, List<PhotoTag> addedTags, List<PhotoTag> removedTags) {
      this.uid = uid;
      this.task = task;
      this.addedTags = addedTags;
      this.removedTags = removedTags;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      tripImagesInteractor.editPhotoActionPipe().createObservableResult(new EditPhotoCommand(uid, task))
            .map(editPhotoCommand -> {
               Photo photo = editPhotoCommand.getResult();
               addedTags.removeAll(photo.getPhotoTags());
               return photo;
            })
            .flatMap(photo -> {
               if (addedTags.isEmpty()) return Observable.just(photo);
               return tripImagesInteractor.addPhotoTagsActionPipe()
                     .createObservableResult(new AddPhotoTagsCommand(uid, addedTags))
                     .map(result -> photo);
            })
            .flatMap(photo -> {
               if (removedTags.isEmpty()) return Observable.just(photo);
               List<Integer> userIds = Queryable.from(removedTags)
                     .concat(photo.getPhotoTags())
                     .map(photoTag -> photoTag
                     .getUser()
                     .getId()).toList();
               return tripImagesInteractor.deletePhotoTagsPipe()
                     .createObservableResult(new DeletePhotoTagsCommand(uid, userIds))
                     .map(result -> photo);
            })
            .doOnNext(photo -> {
               photo.getPhotoTags().addAll(addedTags);
               photo.getPhotoTags().removeAll(removedTags);
               photo.setPhotoTagsCount(photo.getPhotoTags().size());
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.photo_edit_error;
   }
}
