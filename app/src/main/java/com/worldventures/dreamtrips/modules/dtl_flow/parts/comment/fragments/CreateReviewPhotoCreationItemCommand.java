package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;


import android.content.Context;
import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CreateReviewPhotoCreationItemCommand extends Command<PhotoReviewCreationItem> implements InjectableAction {

   @Inject Context context;

   private PhotoPickerModel photoGalleryModel;
   private MediaAttachment.Source source;

   public CreateReviewPhotoCreationItemCommand(PhotoPickerModel photoGalleryModel, MediaAttachment.Source source) {
      this.photoGalleryModel = photoGalleryModel;
      this.source = source;
   }

   @Override
   protected void run(CommandCallback<PhotoReviewCreationItem> callback) throws Throwable {
      ImageUtils.getBitmap(context, photoGalleryModel.getUri(), 300, 300)
            .compose(bitmapObservable -> Observable.zip(ImageUtils.getRecognizedFaces(context, bitmapObservable),
                  bitmapObservable, Pair::new))
            .map(pair -> {
               PhotoReviewCreationItem item = new PhotoReviewCreationItem();
               item.setId(photoGalleryModel.getUri().hashCode());
               item.setFileUri(photoGalleryModel.getUri().toString());
               item.setFilePath(photoGalleryModel.getAbsolutePath());
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
