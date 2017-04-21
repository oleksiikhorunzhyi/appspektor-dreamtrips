package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import com.worldventures.dreamtrips.api.photos.AddUserTagsToPhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoTagParams;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@CommandAction
public class AddPhotoTagsCommand extends Command implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private String photoUid;
   private List<PhotoTag> photoTags;

   public AddPhotoTagsCommand(String photoUid, List<PhotoTag> photoTags) {
      this.photoUid = photoUid;
      this.photoTags = photoTags;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      Observable.just(mapperyContext.convert(photoTags, PhotoTagParams.class))
            .flatMap(photoTagParams ->
                  janet.createPipe(AddUserTagsToPhotoHttpAction.class)
                        .createObservableResult(new AddUserTagsToPhotoHttpAction(photoUid, photoTagParams))
            )
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
