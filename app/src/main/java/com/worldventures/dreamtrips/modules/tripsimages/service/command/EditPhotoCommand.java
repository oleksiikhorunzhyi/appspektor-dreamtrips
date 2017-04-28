package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.api.photos.UpdatePhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoUpdateParams;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class EditPhotoCommand extends Command<Photo> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   private String uid;
   private UploadTask task;

   public EditPhotoCommand(String uid, UploadTask task) {
      this.uid = uid;
      this.task = task;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      Observable.just(mapperyContext.convert(task, PhotoUpdateParams.class))
            .flatMap(photoUpdateParams ->
                  janet.createPipe(UpdatePhotoHttpAction.class)
                        .createObservableResult(new UpdatePhotoHttpAction(uid, photoUpdateParams))
            )
            .map(updatePhotoHttpAction -> mapperyContext.convert(updatePhotoHttpAction.response(), Photo.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
