package com.worldventures.dreamtrips.modules.tripsimages.service.command;


import android.content.Context;
import android.net.Uri;
import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CreatePhotoCreationItemCommand extends Command<PhotoCreationItem> implements InjectableAction {

   @Inject Context context;

   private PhotoGalleryModel photoGalleryModel;
   private MediaAttachment.Source source;

   public CreatePhotoCreationItemCommand(PhotoGalleryModel photoGalleryModel, MediaAttachment.Source source) {
      this.photoGalleryModel = photoGalleryModel;
      this.source = source;
   }

   @Override
   protected void run(CommandCallback<PhotoCreationItem> callback) throws Throwable {
      ImageUtils.getBitmap(context, Uri.parse(photoGalleryModel.getImageUri()), 300, 300)
            .compose(bitmapObservable -> Observable.zip(ImageUtils.getRecognizedFaces(context, bitmapObservable),
                  bitmapObservable, Pair::new))
            .map(pair -> {
               PhotoCreationItem item = new PhotoCreationItem();
               item.setId(photoGalleryModel.getImageUri().hashCode());
               item.setFileUri(photoGalleryModel.getImageUri());
               item.setFilePath(photoGalleryModel.getAbsolutePath());
               item.setStatus(ActionState.Status.START);
               Size imageSize = photoGalleryModel.getSize();
               item.setWidth(imageSize != null ? imageSize.getWidth() : pair.second.getWidth());
               item.setHeight(imageSize != null ? imageSize.getHeight() : pair.second.getHeight());
               item.setSuggestions(pair.first);
               item.setSource(source);
               item.setCanDelete(true);
               item.setCanEdit(true);
               return item;
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
