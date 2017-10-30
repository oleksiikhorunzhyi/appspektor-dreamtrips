package com.worldventures.dreamtrips.social.ui.feed.service.command;

import android.net.Uri;
import android.util.Pair;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.modules.picker.util.CapturedRowMediaHelper;
import com.worldventures.core.utils.Size;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.FetchLocationFromExifCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class ProcessAttachmentsAndPost extends Command<List<PhotoCreationItem>> implements InjectableAction {

   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject CapturedRowMediaHelper capturedRowMediaHelper;

   private String text;
   private List<PhotoCreationItem> images;
   private Uri videoUri;
   private Location location;
   private CreateEntityBundle.Origin origin;

   public ProcessAttachmentsAndPost(String text, List<PhotoCreationItem> images, Uri videoUri, Location location, CreateEntityBundle.Origin origin) {
      this.text = text;
      this.images = images;
      this.videoUri = videoUri;
      this.location = location;
      this.origin = origin;
   }

   @Override
   protected void run(CommandCallback<List<PhotoCreationItem>> commandCallback) throws Throwable {
      Observable.from(images)
            .map(image -> {
               Pair<String, Size> pair = capturedRowMediaHelper.generateUri(image.getFilePath());
               image.setHeight(pair.second.getHeight());
               image.setWidth(pair.second.getWidth());
               image.setFilePath(pair.first);
               return image;
            })
            .concatMap(item -> tripImagesInteractor.fetchLocationFromExifPipe()
                  .createObservableResult(new FetchLocationFromExifCommand(item.getFilePath()))
                  .map(command -> {
                     item.setLocationFromExif(command.getResult());
                     return item;
                  }))
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(creationItems ->
                  postsInteractor.createPostCompoundOperationPipe()
                        .send(new CreatePostCompoundOperationCommand(text, creationItems,
                              videoUri != null ? videoUri.getPath() : null, location, origin)));
   }
}
